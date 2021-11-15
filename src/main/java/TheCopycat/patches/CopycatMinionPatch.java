package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SwitchSubstituteAction;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.powers.DoubleTimePower;
import TheCopycat.stances.ProtectiveStance;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import TheCopycat.utils.GameLogicUtils;
import TheCopycat.vfx.CopycatFlashTargetArrowEffect;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ShowMoveNameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.combat.FlashIntentEffect;
import javassist.CtBehavior;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import kobting.friendlyminions.enums.MonsterIntentEnum;
import kobting.friendlyminions.helpers.BasePlayerMinionHelper;
import kobting.friendlyminions.helpers.MinionConfigHelper;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.patches.MonsterIntentPatch;
import kobting.friendlyminions.patches.MonsterSetMovePatch;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;
import kobting.friendlyminions.patches.PlayerMethodPatches;

import java.util.ArrayList;


public class CopycatMinionPatch {
	public static float noChangeMagicNumber = -320.0f;
	static float tempChance = noChangeMagicNumber;

	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CopycatModMain.makeID("MonsterIntent"));
	private static final String[] TEXT = uiStrings == null ? AbstractMonster.TEXT : uiStrings.TEXT;

	public static float calcMinionTargetChance() {
		if (AbstractDungeon.player != null && AbstractDungeon.player.stance.ID.equals(ProtectiveStance.STANCE_ID)) {
			return 0;
		}
		ArrayList<AbstractMonster> list = BetterFriendlyMinionsUtils.getMinionList();
		long copycatMinions = list.stream().filter(m -> m instanceof AbstractCopycatMinion).count();
		if (copycatMinions > 0) {
			float newChance = copycatMinions * 0.25f;
			if (copycatMinions == list.size() || newChance > MinionConfigHelper.MinionAttackTargetChance) {
				return newChance;
			}
		}
		return MinionConfigHelper.MinionAttackTargetChance;
	}

	@SpirePatch2(clz = AbstractCreature.class, method = "applyEndOfTurnTriggers")
	public static class EndOfTurnPatch {
		@SpirePrefixPatch
		public static void Prefix(AbstractCreature __instance) {
			if (__instance == AbstractDungeon.player) {
				BetterFriendlyMinionsUtils.getMinionList().forEach(m -> {
					if (m instanceof AbstractCopycatMinion) {

						AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
							@Override
							public void update() {
								AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, BetterFriendlyMinionsUtils.minionAiRng);

								for (int i = 0; i < ((AbstractCopycatMinion) m).getMoveCount(); i++) {
									((AbstractCopycatMinion) m).doMove(target);
								}

								if (m instanceof MirrorMinion) {
									AbstractMonster intentMonster = ((MirrorMinion) m).origMonster;
									if (intentMonster.intent != AbstractMonster.Intent.NONE) {
										AbstractDungeon.actionManager.addToTop(new IntentFlashAction(m) {
											@Override
											public void update() {
												if (duration == startDuration) {
													AbstractDungeon.effectList.add(new FlashIntentEffect(ReflectionHacks.getPrivate(intentMonster, AbstractMonster.class, "intentImg"), m));
													intentMonster.intentAlphaTarget = 0.0F;
												}

												tickDuration();
											}
										});
										AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
											@Override
											public void update() {
												isDone = true;
											}
										});
										AbstractDungeon.actionManager.addToTop(new ShowMoveNameAction(intentMonster) {
											@Override
											public void update() {
												intentMonster.isDying = false;
												super.update();
												intentMonster.isDying = true;
											}
										});
										if (GameLogicUtils.checkIntent(intentMonster, 1) || GameLogicUtils.checkIntent(intentMonster, 3)) {
											AbstractDungeon.actionManager.addToTop(new VFXAction(new CopycatFlashTargetArrowEffect(m, target, 0.7f), 0.05f));
										}
									}
								} else {
									if (m.intent != AbstractMonster.Intent.NONE) {
										AbstractDungeon.actionManager.addToTop(new IntentFlashAction(m));
										AbstractDungeon.actionManager.addToTop(new ShowMoveNameAction(m));
										if (GameLogicUtils.checkIntent(m, 1) || GameLogicUtils.checkIntent(m, 3)) {
											AbstractDungeon.actionManager.addToTop(new VFXAction(new CopycatFlashTargetArrowEffect(m, target, 0.7f), 0.05f));
										}
									}
								}
								isDone = true;
							}
						});
						AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
							@Override
							public void update() {
								m.powers.forEach((power) -> {
									power.atEndOfTurn(true);
								});
								isDone = true;
							}
						});
					}
				});

				if (!SubstituteMinion.instance.isDead) {
					AbstractDungeon.actionManager.addToBottom(new SwitchSubstituteAction(true));
				}
			}
		}
	}

	@SpirePatch2(clz = MonsterGroup.class, method = "showIntent")
	public static class ShowIntentPatch {
		@SpirePostfixPatch
		public static void Postfix() {
			BetterFriendlyMinionsUtils.getMinionList().forEach(m -> {
				if (m instanceof AbstractCopycatMinion) {
					AbstractMonster intentMonster = m instanceof MirrorMinion ? ((MirrorMinion) m).origMonster : m;
					MirrorMinion.disableMonsterLogic = true;
					intentMonster.createIntent();
					MirrorMinion.disableMonsterLogic = false;
				}
			});
		}
	}

	@SpirePatch2(clz = MonsterSetMovePatch.class, method = "maybeChangeIntent")
	public static class AdjustChangeIntentPatch {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(AbstractMonster monster) {
			if (monster instanceof AbstractCopycatMinion || MirrorMinion.disableMonsterLogic) {
				return SpireReturn.Return();
			}
			for (int i = 0; i < 4; i++) {
				if (BetterFriendlyMinionsUtils.copycatMinions[i] instanceof MirrorMinion && ((MirrorMinion) BetterFriendlyMinionsUtils.copycatMinions[i]).origMonster == monster) {
					return SpireReturn.Return();
				}
			}

			float newChance = calcMinionTargetChance();
			tempChance = MinionConfigHelper.MinionAttackTargetChance;
			MinionConfigHelper.MinionAttackTargetChance = newChance;

			return SpireReturn.Continue();
		}

		@SpirePostfixPatch
		public static void Postfix() {
			if (tempChance != noChangeMagicNumber) {
				MinionConfigHelper.MinionAttackTargetChance = tempChance;
				tempChance = noChangeMagicNumber;
			}
		}
	}

	@SpirePatch2(clz = PlayerMethodPatches.DamagePatch.class, method = "Prefix")
	public static class DisableThornsRedirectPatch {
		@SpirePrefixPatch
		public static SpireReturn<SpireReturn<Void>> Prefix(DamageInfo info) {
			if (!AbstractDungeon.actionManager.turnHasEnded) {
				if (info.owner instanceof AbstractMonster) {
					AbstractFriendlyMonster target = MonsterHelper.getTarget((AbstractMonster) info.owner);
					if (target instanceof AbstractCopycatMinion) {
						return SpireReturn.Return(SpireReturn.Continue());
					}
				}
			} else {
				if (info.owner instanceof AbstractMonster) {
					AbstractFriendlyMonster target = MonsterHelper.getTarget((AbstractMonster) info.owner);
					if (target instanceof SubstituteMinion) {
						AbstractDungeon.actionManager.addToBottom(new DamageAction(target, info, AbstractGameAction.AttackEffect.NONE));
						return SpireReturn.Return(SpireReturn.Return());
					}
				}
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch2(clz = AbstractDungeon.class, method = "onModifyPower")
	public static class MinionUpdatePowersPatch {
		@SpirePostfixPatch
		public static void Postfix() {
			for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
				if (m instanceof AbstractCopycatMinion) {
					m.applyPowers();
				}
			}
		}
	}

	@SpirePatch2(clz = UseCardAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractCard.class, AbstractCreature.class})
	public static class OnUseCardPatch {
		@SpirePostfixPatch
		public static void Postfix(UseCardAction __instance, AbstractCard card) {
			for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
				if (m instanceof AbstractCopycatMinion) {
					for (AbstractPower p : m.powers) {
						p.onUseCard(card, __instance);
					}
				}
			}
		}
	}

	@SpirePatch2(clz = UseCardAction.class, method = "update")
	public static class AfterUseCardPatch {
		@SpireInsertPatch(locator = AfterUseCardPatchLocator.class)
		public static void Insert(UseCardAction __instance, AbstractCard ___targetCard) {
			for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
				if (m instanceof AbstractCopycatMinion) {
					for (AbstractPower p : m.powers) {
						p.onAfterUseCard(___targetCard, __instance);
					}
				}
			}
		}

		public static class AfterUseCardPatchLocator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "freeToPlayOnce");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}

	/*
	public static Texture getIntentImage(AbstractMonster.Intent intent, int dmg) {
		if (intent == MonsterIntentEnum.ATTACK_MINION) {
			if (dmg < 5) {
				return INTENT_ATK_MINION_TIP_1;
			} else if (dmg < 10) {
				return INTENT_ATK_MINION_TIP_2;
			} else if (dmg < 15) {
				return INTENT_ATK_MINION_TIP_3;
			} else if (dmg < 20) {
				return INTENT_ATK_MINION_TIP_4;
			} else if (dmg < 25) {
				return INTENT_ATK_MINION_TIP_5;
			} else {
				return dmg < 30 ? INTENT_ATK_MINION_TIP_6 : INTENT_ATK_MINION_TIP_7;
			}
		} else if (intent == MonsterIntentEnum.ATTACK_MINION_BUFF) {
			return INTENT_ATTACK_MINION_BUFF;
		} else if (intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF) {
			return INTENT_ATTACK_MINION_DEBUFF;
		} else if (intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {
			return INTENT_ATTACK_MINION_DEFEND;
		}
		return ImageMaster.INTENT_UNKNOWN;
	}

	@SpirePatch2(clz = MonsterIntentPatch.GetIntentImagePatch.class, method = "Prefix")
	public static class FriendlyMinionsIntentImagePatchPatch {
		@SpirePrefixPatch
		public static SpireReturn<SpireReturn<Texture>> Prefix(AbstractMonster _____instance) {
			if (MonsterHelper.getTarget(_____instance) instanceof AbstractCopycatMinion) {
				AbstractMonster.Intent intent = _____instance.intent;
				if (intent == MonsterIntentEnum.ATTACK_MINION
						|| intent == MonsterIntentEnum.ATTACK_MINION_BUFF
						|| intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF
						|| intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {
					int dmg = 0;
					if (intent == MonsterIntentEnum.ATTACK_MINION) {
						dmg = ReflectionHacks.getPrivate(_____instance, AbstractMonster.class, "intentDmg");
						if (ReflectionHacks.getPrivate(_____instance, AbstractMonster.class, "isMultiDmg")) {
							dmg *= ReflectionHacks.<Integer>getPrivate(_____instance, AbstractMonster.class, "intentMultiAmt");
						}
					}

					// The following code doesn't work:
					//   return SpireReturn.Return(SpireReturn.Return(getIntentImage(intent, dmg)));
					// because both SpireReturn objects become SpireReturn.PLACEHOLDER .

					SpireReturn<Texture> ret1 = SpireReturn.Return(getIntentImage(intent, dmg));
					Constructor<SpireReturn> constructor;
					try {
						constructor = SpireReturn.class.getDeclaredConstructor(Object.class);
						constructor.setAccessible(true);

						return constructor.newInstance(ret1);
					} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				return SpireReturn.Return(SpireReturn.Continue());
			}
			return SpireReturn.Continue();
		}
	}
	*/

	@SpirePatch2(clz = MonsterIntentPatch.UpdateIntentTipPatch.class, method = "Prefix")
	public static class FriendlyMinionsIntentTipPatchPatch {
		public static String highlightName(String name) {
			return name.replaceAll("(?<=\\s|^)(?=\\S)", "#g");
		}

		@SpireInsertPatch(locator = Locator.class)
		public static SpireReturn<SpireReturn<Void>> Insert(AbstractMonster _____instance, AbstractFriendlyMonster ___target, AbstractMonster.Intent ___intent, PowerTip ___intentTip, boolean ___isMultiDamage, int ___intentDmg, int ___intentMultiAmt) {
			if (___target instanceof AbstractCopycatMinion) {
				if (___intent == MonsterIntentEnum.ATTACK_MINION) {
					___intentTip.header = TEXT[0];
					if (___isMultiDamage) {
						___intentTip.body = String.format(TEXT[1] + ___intentDmg + TEXT[2] + ___intentMultiAmt + TEXT[3], highlightName(___target.name));
					} else {
						___intentTip.body = String.format(TEXT[4] + ___intentDmg + TEXT[5], highlightName(___target.name));
					}
					int tmp;
					if (___isMultiDamage) {
						tmp = ___intentDmg * ___intentMultiAmt;
					} else {
						tmp = ___intentDmg;
					}

					if (tmp < 5) {
						___intentTip.img = ImageMaster.INTENT_ATK_TIP_1;
					} else if (tmp < 10) {
						___intentTip.img = ImageMaster.INTENT_ATK_TIP_2;
					} else if (tmp < 15) {
						___intentTip.img = ImageMaster.INTENT_ATK_TIP_3;
					} else if (tmp < 20) {
						___intentTip.img = ImageMaster.INTENT_ATK_TIP_4;
					} else if (tmp < 25) {
						___intentTip.img = ImageMaster.INTENT_ATK_TIP_5;
					} else {
						___intentTip.img = tmp < 30 ? ImageMaster.INTENT_ATK_TIP_6 : ImageMaster.INTENT_ATK_TIP_7;
					}
				} else if (___intent == MonsterIntentEnum.ATTACK_MINION_BUFF) {
					___intentTip.header = TEXT[6];
					if (___isMultiDamage) {
						___intentTip.body = String.format(TEXT[7] + ___intentDmg + TEXT[2] + ___intentMultiAmt + TEXT[8], highlightName(___target.name));
					} else {
						___intentTip.body = String.format(TEXT[9] + ___intentDmg + TEXT[5], highlightName(___target.name));
					}

					___intentTip.img = ImageMaster.INTENT_ATTACK_BUFF;

				} else if (___intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF) {
					___intentTip.header = TEXT[10];
					___intentTip.body = String.format(TEXT[11] + ___intentDmg + TEXT[5], highlightName(___target.name));
					___intentTip.img = ImageMaster.INTENT_ATTACK_DEBUFF;

				} else if (___intent == MonsterIntentEnum.ATTACK_MINION_DEFEND) {
					___intentTip.header = TEXT[0];
					if (___isMultiDamage) {
						___intentTip.body = String.format(TEXT[12] + ___intentDmg + TEXT[2] + ___intentMultiAmt + TEXT[3], highlightName(___target.name));
					} else {
						___intentTip.body = String.format(TEXT[12] + ___intentDmg + TEXT[5], highlightName(___target.name));
					}
					___intentTip.img = ImageMaster.INTENT_ATTACK_DEFEND;

				} else {
					return SpireReturn.Return(SpireReturn.Continue());
				}
				ReflectionHacks.setPrivate(_____instance, AbstractMonster.class, "intentTip", ___intentTip);
				return SpireReturn.Return(SpireReturn.Return());
			} else {
				return SpireReturn.Continue();
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterHelper.class, "getTarget");
				return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
			}
		}
	}

	@SpirePatch2(clz = MonsterIntentPatch.CreateIntentPatch.class, method = "Insert")
	public static class SeededRandomTargetAndIntentIconBugFixAndSubstituteRedirectPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractMonster _____instance) {
			if (!(_____instance instanceof AbstractFriendlyMonster)) {
				AbstractFriendlyMonster target = MonsterHelper.getTarget(_____instance);
				if (target != null) {
					MonsterGroup minions = BetterFriendlyMinionsUtils.getMonsterGroup();
					boolean patch = false;
					for (AbstractMonster m : minions.monsters) {
						if (m instanceof AbstractCopycatMinion) {
							patch = true;
							break;
						}
					}
					if (patch) {
						AbstractFriendlyMonster m = (AbstractFriendlyMonster) minions.getRandomMonster(null, true, BetterFriendlyMinionsUtils.enemyTargetRng);
						MonsterHelper.setTarget(_____instance, m);
						_____instance.applyPowers();
						DoubleTimePower.updateMinions();
					}
				} else if (!SubstituteMinion.instance.isDead) {
					if (MirrorMinion.disableMonsterLogic) return;
					BetterFriendlyMinionsUtils.switchTarget(_____instance, SubstituteMinion.instance, true);
					DoubleTimePower.updateMinions();
				}
			}
		}
	}

	@SpirePatch2(clz = AbstractMonster.class, method = "die", paramtypez = boolean.class)
	public static class DoubleTimePowerDiePatch {
		@SpirePostfixPatch
		public static void Postfix() {
			DoubleTimePower.updateMinions();
		}
	}

	@SpirePatch2(clz = PlayerMethodPatches.RenderPatch.class, method = "Prefix")
	public static class EventCombatRenderFix {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(AbstractPlayer _instance, SpriteBatch sb) {
			if (!(_instance instanceof AbstractPlayerWithMinions)) {
				MonsterGroup minions;
				minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);

				if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
					if (BasePlayerMinionHelper.hasMinions(AbstractDungeon.player)) {
						minions.render(sb);
					}
				}
			}
			return SpireReturn.Return();
		}
	}

	@SpirePatch2(clz = PlayerMethodPatches.UpdatePatch.class, method = "Postfix")
	public static class EventCombatUpdateFix {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(AbstractPlayer _instance) {
			if (!(_instance instanceof AbstractPlayerWithMinions)) {
				MonsterGroup minions;
				minions = PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player);

				if (AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
					if (BasePlayerMinionHelper.hasMinions(AbstractDungeon.player)) {
						minions.update();
					}
				}
			}
			return SpireReturn.Return();
		}
	}
}
