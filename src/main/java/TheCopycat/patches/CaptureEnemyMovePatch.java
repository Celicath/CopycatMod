package TheCopycat.patches;

import TheCopycat.cards.monster.*;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import TheCopycat.utils.MonsterCardMoveInfo;
import TheCopycat.utils.Shader;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.unique.GainBlockRandomMonsterAction;
import com.megacrit.cardcrawl.actions.unique.SummonGremlinAction;
import com.megacrit.cardcrawl.actions.unique.VampireDamageAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CtBehavior;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;
import java.util.HashMap;

public class CaptureEnemyMovePatch {
	public static HashMap<String, AbstractMonsterCard> generatedCards = new HashMap<>();
	public static HashMap<AbstractMonster, AbstractMonsterCard> lastMoveCards = new HashMap<>();
	public static SpriteBatch sb = new SpriteBatch();
	public static int WIDTH = 500;
	public static int HEIGHT = 380;
	public static float CAMERA_WIDTH = 350 * Settings.scale;
	public static float CAMERA_HEIGHT = 266 * Settings.scale;
	public static HashMap<String, String> powerCodeMap = new HashMap<String, String>() {
		{
			put(StrengthPower.POWER_ID, "S");
			put(DexterityPower.POWER_ID, "D");
			put(PlatedArmorPower.POWER_ID, "P");
			put(ThornsPower.POWER_ID, "T");
			put(SharpHidePower.POWER_ID, "T");
			put(RitualPower.POWER_ID, "R");
			put(FrailPower.POWER_ID, "2P");
			put(VulnerablePower.POWER_ID, "V");
			put(WeakPower.POWER_ID, "W");
			put(PoisonPower.POWER_ID, "P");
			put(ConstrictedPower.POWER_ID, "P");
			put(EntanglePower.POWER_ID, "E");
			put(MetallicizePower.POWER_ID, "M");
			put(DrawReductionPower.POWER_ID, "H");
		}
	};
	static AbstractMonster curMonster = null;
	static AbstractFriendlyMonster enemyTarget = null;
	static AbstractMonsterCard curCard = null;
	static EnemyMoveInfo curMove = null;
	static String id;
	static boolean suicide = false;
	static boolean hasUniqueName = false;
	static int magicMultiplier = 1;
	// render
	static OrthographicCamera camera;
	static FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, WIDTH, HEIGHT, false);

	static {
		camera = new OrthographicCamera(WIDTH, HEIGHT);
		camera.position.set(WIDTH / 2.0F, HEIGHT / 2.0F, 0.0F);
		camera.update();
		sb.setProjectionMatrix(camera.combined);
		camera.viewportWidth = CAMERA_WIDTH;
		camera.viewportHeight = CAMERA_HEIGHT;
	}

	@SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
	public static class TakeTurnCapture {
		@SpireInsertPatch(locator = BeforeTakeTurnLocator.class)
		public static void Insert(AbstractMonster ___m) {
			curMonster = ___m;
			curMove = ReflectionHacks.getPrivate(___m, AbstractMonster.class, "move");
			if (curMove instanceof MonsterCardMoveInfo) {
				BetterFriendlyMinionsUtils.hijackActionQueue(null, BetterFriendlyMinionsUtils.HijackMode.MANIPULATE);
				if (curMonster != null) {
					BetterFriendlyMinionsUtils.origActions.add(new ShowMoveNameAction(curMonster));
					BetterFriendlyMinionsUtils.origActions.add(new IntentFlashAction(curMonster));
				}
				return;
			}

			AbstractFriendlyMonster target = MonsterHelper.getTarget(___m);
			if (target instanceof AbstractCopycatMinion) {
				enemyTarget = target;
				BetterFriendlyMinionsUtils.hijackActionQueue(target, BetterFriendlyMinionsUtils.HijackMode.FRIENDLY_MINION_DEFEND);
			}

			int moveId = curMove.nextMove;
			id = ___m.id + "_" + curMove.nextMove;
			suicide = false;

			/* Special enemy moves */
			if (___m instanceof GremlinWizard && moveId == 1) {
				curCard = new UltimateBlast();
			} else if (___m instanceof GremlinWizard && moveId == 2) {
				curCard = new Charging();
			} else if (___m instanceof GremlinNob && moveId == 3) {
				curCard = new Enrage();
			} else if (___m instanceof Byrd && moveId == 2) {
				curCard = new Fly();
			} else if (___m instanceof Healer && moveId == 2) {
				curCard = new AreaHeal();
			} else if ((___m instanceof Looter || ___m instanceof Mugger) && moveId == 3) {
				curCard = new Escape();
			} else if (___m instanceof Darkling && moveId == 5 || ___m instanceof AwakenedOne && moveId == 3) {
				curCard = new Reincarnate();
			} else if (___m instanceof Chosen && moveId == 4) {
				curCard = new Hex();
			} else if (___m instanceof Snecko && moveId == 1) {
				curCard = new PerplexingEye();
			} else if (___m instanceof Maw && moveId == 5) {
				curCard = new Nom();
			} else if (___m instanceof WrithingMass && moveId == 4) {
				curCard = new Implant();
			} else if (___m instanceof Transient && moveId == 1) {
				curCard = new FadingAttack();
			} else if (___m instanceof BookOfStabbing && moveId == 1) {
				if (AbstractDungeon.ascensionLevel < 18) {
					curCard = new MultiStabbing();
				} else {
					curCard = new MultiStabbing2();
				}
			} else if (___m instanceof GiantHead && moveId == 2) {
				curCard = new ItIsTime();
			} else if (___m instanceof Hexaghost && moveId == 1) {
				curCard = new Divider();
			} else if (___m instanceof Hexaghost && moveId == 6) {
				curCard = new Inferno();
			} else if (___m instanceof Champ && moveId == 7) {
				curCard = new ChampsAnger();
			} else if (___m instanceof TimeEater && moveId == 5) {
				if (AbstractDungeon.ascensionLevel < 19) {
					curCard = new Haste();
				} else {
					curCard = new Haste2();
				}
			} else if (___m instanceof CorruptHeart && moveId == 3) {
				curCard = new Debilitate();
			} else {
				/* DynamicCard */
				DynamicCard genCard = new DynamicCard();

				magicMultiplier = 1;
				if (___m.moveName != null) {
					hasUniqueName = true;
					genCard.originalName = ___m.moveName;
				} else {
					hasUniqueName = false;
					genCard.originalName = ___m.name + DynamicCard.EXTENDED_DESCRIPTION[DynamicCard.EXTENDED_DESCRIPTION.length - 1] + curMove;
				}
				genCard.name = genCard.originalName;

				genCard.baseDamage = curMove.baseDamage;
				if (curMove.isMultiDamage) {
					genCard.hits = curMove.multiplier;
				}
				if (curMove.baseDamage > 0 && ___m.hasPower(ThieveryPower.POWER_ID)) {
					genCard.stealGold = true;
				}
				curCard = genCard;
			}
		}

		@SpirePostfixPatch
		public static void Postfix(GameActionManager __instance) {
			BetterFriendlyMinionsUtils.HijackMode hijackMode = BetterFriendlyMinionsUtils.revertHijack();
			if (hijackMode == BetterFriendlyMinionsUtils.HijackMode.MANIPULATE) {

				// Manipulated intent logic
				if (curMonster != null && curMove instanceof MonsterCardMoveInfo) {
					ArrayList<AbstractGameAction> origActions = AbstractDungeon.actionManager.actions;
					AbstractDungeon.actionManager.actions = BetterFriendlyMinionsUtils.origActions;

					AbstractDungeon.actionManager.actions.add(new AnimateSlowAttackAction(curMonster));

					AbstractCreature target = BetterFriendlyMinionsUtils.getTarget(curMonster);
					curCard = ((MonsterCardMoveInfo) curMove).attachedCard;
					curCard.monsterTurnApplyPowers(curMonster, target);
					curCard.monsterTakeTurn(curMonster, target, false);

					for (AbstractGameAction action : origActions) {
						if (action instanceof RollMoveAction || action instanceof SetMoveAction) {
							BetterFriendlyMinionsUtils.origActions.add(action);
						}
					}
				}
			} else {
				if (hijackMode == BetterFriendlyMinionsUtils.HijackMode.FRIENDLY_MINION_DEFEND) {
					// Minion target logic
					ArrayList<AbstractGameAction> newActions = AbstractDungeon.actionManager.actions;
					for (int i = 0; i < newActions.size(); i++) {
						AbstractGameAction a = newActions.get(i);
						if (a instanceof DamageAction) {
							DamageInfo info = ReflectionHacks.getPrivate(a, DamageAction.class, "info");
							AbstractStance tmp = AbstractDungeon.player.stance;
							AbstractDungeon.player.stance = new NeutralStance();
							info.applyPowers(curMonster, enemyTarget);
							info.owner = curMonster;
							a.source = curMonster;
							a.target = enemyTarget;
							AbstractDungeon.player.stance = tmp;
						} else if (a instanceof ApplyPowerAction) {
							if (a.target instanceof AbstractPlayer) {
								AbstractPower power = ReflectionHacks.getPrivate(a, ApplyPowerAction.class, "powerToApply");

								if (power instanceof PoisonPower ||
									power instanceof WeakPower ||
									power instanceof FrailPower ||
									power instanceof VulnerablePower ||
									power instanceof StrengthPower ||
									power instanceof DexterityPower ||
									power instanceof ConstrictedPower ||
									power instanceof GainStrengthPower ||
									power instanceof LoseStrengthPower ||
									power instanceof LoseDexterityPower
								) {
									power.owner = enemyTarget;
									a.target = enemyTarget;
								} else {
									newActions.set(i, new TextAboveCreatureAction(enemyTarget, ApplyPowerAction.TEXT[1]));
								}
							}
						}
					}
					AbstractDungeon.actionManager.actions = BetterFriendlyMinionsUtils.origActions;
					AbstractDungeon.actionManager.actions.addAll(newActions);
					lastMoveCards.put(curMonster, curCard);
				}

				// Create monster card logic
				if (curCard == null) return;
				do {
					if (curCard instanceof DynamicCard) {
						DynamicCard genCard = (DynamicCard) curCard;
						if (!genCard.isDraw && !genCard.stealGold && genCard.buffs.isEmpty()) {
							if (genCard.debuffs.equals("E")) {
								genCard.setMagicNumber(10);
							} else if (genCard.debuffs.length() == 1) {
								genCard.setMagicNumber(genCard.magicNumber * magicMultiplier);
							}
						}
						if (genCard.debuffs.indexOf('E') != -1 && genCard.debuffs.indexOf('S') != -1) {
							genCard.debuffs = genCard.debuffs.replace("E", "");
						}
						genCard.setType();
						genCard.calculateCost();
						if (curMonster instanceof Champ && curMove.nextMove == 2) {
							if (AbstractDungeon.ascensionLevel < 9) {
								genCard.baseCost = genCard.cost = 2;
							} else if (AbstractDungeon.ascensionLevel < 19) {
								genCard.baseCost = genCard.cost = 3;
							} else {
								genCard.baseCost = genCard.cost = 4;
							}
						}
						if (genCard.stealGold && genCard.baseMagicNumber <= 0) {
							genCard.setMagicNumber(3 + genCard.baseCost * 2);
						}
						genCard.calculateMonsterCardID();
						if (genCard.empty) {
							break;
						}
						genCard.updateDescription();
					} else if (curCard instanceof SummonMonsterCard) {
						((SummonMonsterCard) curCard).calculateMonsterCardID();
					} else {
						if (!UnlockTracker.isCardSeen(curCard.cardID)) {
							UnlockTracker.markCardAsSeen(curCard.cardID);
						}
					}
					lastMoveCards.put(curMonster, curCard);
					if (curCard instanceof Scammed) {
						break;
					}
					generatedCards.put(id, curCard);

					if (!curCard.loadTexture(id)) {
						float scale = Math.min(1, curMonster.hb.width * 1.2f / CAMERA_WIDTH);
						scale = Math.min(scale, curMonster.hb.width * 2.0f / CAMERA_HEIGHT);
						scale = Math.max(scale, curMonster.hb.width / CAMERA_WIDTH);
						scale = Math.max(scale, curMonster.hb.height / CAMERA_HEIGHT);

						camera.viewportWidth = CAMERA_WIDTH * scale;
						camera.viewportHeight = CAMERA_HEIGHT * scale;
						camera.position.set(
							curMonster.hb.cX,
							curMonster.hb.cY - curMonster.hb.height * 0.1f + Math.max((curMonster.hb.height - CAMERA_HEIGHT * scale * Settings.scale) / 2, 0),
							0.0f);
						camera.update();

						Matrix4 sbMat = sb.getProjectionMatrix().cpy();
						sb.setProjectionMatrix(camera.combined);
						Matrix4 psbMat = CardCrawlGame.psb.getProjectionMatrix().cpy();
						CardCrawlGame.psb.setProjectionMatrix(camera.combined);

						fbo.begin();
						Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
						Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
						Gdx.gl.glColorMask(true, true, true, true);

						sb.begin();
						MonsterRenderPatch.RenderForMonsterCardPatch.shouldPatch = true;
						curMonster.render(sb);
						MonsterRenderPatch.RenderForMonsterCardPatch.shouldPatch = false;
						sb.end();

						CardCrawlGame.psb.setProjectionMatrix(psbMat);
						sb.setProjectionMatrix(sbMat);

						Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());
						Texture texture = new Texture(pixmap);
						pixmap.dispose();
						fbo.end();

						fbo.begin();
						sb.setShader(Shader.tintGradientShader);
						switch (curMonster.intent) {
							case ATTACK:
								sb.setColor(1.0f, 0.0f, 0.0f, 1.0f);
								break;
							case BUFF:
								sb.setColor(0.0f, 1.0f, 0.0f, 1.0f);
								break;
							case MAGIC:
							case DEBUFF:
							case STRONG_DEBUFF:
								sb.setColor(0.3f, 1.0f, 0.6f, 1.0f);
								break;
							case DEFEND:
								sb.setColor(0.0f, 0.0f, 1.0f, 1.0f);
								break;
							case SLEEP:
							case ESCAPE:
							case UNKNOWN:
								sb.setColor(1.0f, 1.0f, 1.0f, 1.0f);
								break;
							case ATTACK_BUFF:
								sb.setColor(0.75f, 0.75f, 0.0f, 1.0f);
								break;
							case DEFEND_BUFF:
								sb.setColor(0.0f, 0.75f, 0.75f, 1.0f);
								break;
							case ATTACK_DEBUFF:
								sb.setColor(0.85f, 0.6f, 0.4f, 1.0f);
								break;
							case DEFEND_DEBUFF:
								sb.setColor(0.2f, 0.6f, 0.95f, 1.0f);
								break;
							case ATTACK_DEFEND:
								sb.setColor(0.75f, 0.0f, 0.75f, 1.0f);
								break;
							default:
								switch (curCard.type) {
									case ATTACK:
										sb.setColor(1.0f, 0.0f, 0.0f, 1.0f);
										break;
									case POWER:
										sb.setColor(0.0f, 1.0f, 0.0f, 1.0f);
										break;
									default:
										sb.setColor(0.0f, 0.0f, 1.0f, 1.0f);
								}
						}
						sb.begin();
						Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
						Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
						sb.draw(texture, 0, 0, WIDTH, HEIGHT);
						sb.setShader(null);
						sb.end();

						pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());
						fbo.end();

						AbstractMonsterCard.saveTexture(id, pixmap);
						curCard.loadTexture(id);
					}
				} while (false);
			}

			curMonster = null;
			curCard = null;
			suicide = false;
		}
	}

	private static class BeforeTakeTurnLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractMonster.class, "intent");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch(clz = GameActionManager.class, method = "addToBottom")
	@SpirePatch(clz = GameActionManager.class, method = "addToTop")
	public static class ActionManagerCapture {
		@SpirePrefixPatch
		public static void Prefix(GameActionManager __instance, AbstractGameAction action) {
			if (curMonster == null) return;
			if (action instanceof SuicideAction) {
				suicide = true;
				return;
			}
			if (curCard instanceof DynamicCard) {
				DynamicCard genCard = (DynamicCard) curCard;
				if (action instanceof GainBlockAction || action instanceof GainBlockRandomMonsterAction) {
					genCard.baseBlock = action.amount;
				} else if (action instanceof ApplyPowerAction) {
					AbstractPower p = ReflectionHacks.getPrivate(action, ApplyPowerAction.class, "powerToApply");
					String s = powerCodeMap.get(p.ID);
					if (s != null) {
						int amount = p.amount;
						int i = 0;
						if (action.target instanceof AbstractFriendlyMonster || action.target instanceof AbstractPlayer) {
							for (int len = s.length(); i < len; i++) {
								char c = s.charAt(i);
								if (c >= '2' && c <= '9') {
									magicMultiplier = s.charAt(0) - '0';
								} else if (c == 'D') {
									genCard.addDebuff('V');
								} else if (c == 'H') {
									genCard.isDraw = true;
									amount = Math.max(Math.abs(amount), 2);
								} else {
									genCard.addDebuff(c);
								}
							}
							genCard.setMagicNumber(amount);
						} else if (action.target instanceof AbstractMonster) {
							for (int len = s.length(); i < len; i++) {
								char c = s.charAt(i);
								genCard.addBuff(c);
							}
							genCard.setMagicNumber(amount);
						}
					}
				} else if (action instanceof MakeTempCardInDiscardAction
					|| action instanceof MakeTempCardInDrawPileAction
					|| action instanceof MakeTempCardInDiscardAndDeckAction) {
					AbstractCard c;
					int numCards;
					if (action instanceof MakeTempCardInDiscardAction) {
						c = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAction.class, "c");
						numCards = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAction.class, "numCards");
					} else if (action instanceof MakeTempCardInDrawPileAction) {
						c = ReflectionHacks.getPrivate(action, MakeTempCardInDrawPileAction.class, "cardToMake");
						numCards = action.amount;
					} else {
						c = ReflectionHacks.getPrivate(action, MakeTempCardInDiscardAndDeckAction.class, "cardToMake");
						numCards = 2;
					}
					if (c instanceof Dazed) {
						genCard.isDraw = true;
						genCard.setMagicNumber(numCards);
					} else if (c instanceof Slimed) {
						genCard.exhaustOther = true;
						if (numCards >= 2) {
							genCard.isDraw = true;
							genCard.setMagicNumber(numCards);
						}
					} else if (c instanceof Wound) {
						genCard.isDiscard = true;
						if (numCards >= 2) {
							genCard.isDraw = true;
							genCard.setMagicNumber(numCards);
						}
					} else if (c instanceof VoidCard) {
						genCard.enterCalm = true;
					} else if (c instanceof Burn) {
						genCard.addDebuff('P');
						genCard.setMagicNumber(numCards * 2);
					}
				} else if (action instanceof VampireDamageAction) {
					genCard.isVampire = true;
				}
			}
		}
	}

	public static void setSummonMonsterCard(AbstractMonster m) {
		boolean setSummon = false;
		SummonMonsterCard sCard = null;
		if (curCard instanceof DynamicCard) {
			sCard = new SummonMonsterCard();
			sCard.setName(hasUniqueName ? curCard.originalName : null, m);
			setSummon = true;
			curCard = sCard;
		} else if (curCard instanceof SummonMonsterCard) {
			sCard = (SummonMonsterCard) curCard;
			if (!sCard.summonID.equals(m.id)) {
				setSummon = AbstractDungeon.miscRng.randomBoolean();
			}
		}
		if (setSummon) {
			if (m.id.equals(TorchHead.ID) || m.id.equals(BronzeOrb.ID) || m.id.equals(SpikeSlime_L.ID) || m.id.equals(AcidSlime_L.ID)) {
				sCard.setSummon(m, 2, 16);
			} else {
				sCard.setSummon(m, 1, 10);
			}
		}
		if (suicide) {
			lastMoveCards.put(m, curCard);
		}
	}

	@SpirePatch2(clz = SpawnMonsterAction.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {AbstractMonster.class, boolean.class, int.class})
	public static class SpawnMonsterCapture {
		@SpirePostfixPatch
		public static void Postfix(AbstractMonster m, boolean isMinion) {
			if (curMonster != null && (isMinion || suicide)) {
				setSummonMonsterCard(m);
			}
		}
	}

	@SpirePatch2(clz = SummonGremlinAction.class, method = SpirePatch.CONSTRUCTOR)
	public static class SummonGremlinCapture {
		@SpirePostfixPatch
		public static void Postfix(AbstractMonster ___m) {
			setSummonMonsterCard(___m);
		}
	}
}
