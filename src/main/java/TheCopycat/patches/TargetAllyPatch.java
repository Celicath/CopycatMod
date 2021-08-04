package TheCopycat.patches;

import TheCopycat.actions.SlimeSlasherAction;
import TheCopycat.cards.SlimeSlasher;
import TheCopycat.interfaces.TargetAllyCard;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TargetAllyPatch {
	public static AbstractCreature hoveredally = null;
	public static HashMap<UUID, AbstractCreature> cardTargetMap = new HashMap<>();

	@SpirePatch2(clz = AbstractPlayer.class, method = "updateSingleTargetInput")
	public static class HoverPatch {
		@SpireInsertPatch(locator = AfterSetHoveredMonsterLocator.class)
		public static void Insert(AbstractPlayer __instance, @ByRef AbstractMonster[] ___hoveredMonster) {
			if (__instance.hoveredCard instanceof TargetAllyCard && __instance.hoveredCard.target == AbstractCard.CardTarget.ENEMY) {
				hoveredally = BetterFriendlyMinionsUtils.getHoveredAlly();
				if (hoveredally == null) {
					___hoveredMonster[0] = null;
				} else {
					___hoveredMonster[0] = BetterFriendlyMinionsUtils.dummy;
				}
			}
		}

		private static class AfterSetHoveredMonsterLocator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "areMonstersBasicallyDead");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "updateTargetArrowWithKeyboard")
	public static class KeyboardSelectPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static SpireReturn<Void> Insert(AbstractPlayer __instance, int ___directionIndex, @ByRef boolean[] ___isUsingClickDragControl, @ByRef AbstractMonster[] ___hoveredMonster) {
			if (__instance.hoveredCard instanceof TargetAllyCard) {
				ArrayList<AbstractCreature> allyList = BetterFriendlyMinionsUtils.getAllyList();

				if (allyList.isEmpty()) {
					return SpireReturn.Return();
				}
				int index = 0;
				for (AbstractCreature c : allyList) {
					if (c.hb.hovered) {
						hoveredally = c;
						break;
					}
					index++;
				}

				AbstractCreature newTarget;
				if (hoveredally == null) {
					if (___directionIndex == 1) {
						newTarget = allyList.get(0);
					} else {
						newTarget = allyList.get(allyList.size() - 1);
					}
				} else {
					int newTargetIndex = index + ___directionIndex;
					newTargetIndex = (newTargetIndex + allyList.size()) % allyList.size();
					newTarget = allyList.get(newTargetIndex);
				}

				Hitbox target = newTarget.hb;
				Gdx.input.setCursorPosition((int) target.cX, Settings.HEIGHT - (int) target.cY);
				hoveredally = newTarget;
				___isUsingClickDragControl[0] = true;
				__instance.isDraggingCard = true;

				if (hoveredally.halfDead) {
					hoveredally = null;
				}
				if (hoveredally == null) {
					___hoveredMonster[0] = null;
				} else {
					___hoveredMonster[0] = BetterFriendlyMinionsUtils.dummy;
				}
				return SpireReturn.Return();
			}
			return SpireReturn.Continue();
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "getCurrRoom");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "playCard")
	public static class PlayCardPatch {
		@SpirePrefixPatch
		public static void Prefix(AbstractPlayer __instance) {
			if (__instance.hoveredCard instanceof TargetAllyCard) {
				cardTargetMap.put(__instance.hoveredCard.uuid, hoveredally);
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "renderHoverReticle")
	public static class ReticlePatch {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(SpriteBatch sb, AbstractCard ___hoveredCard) {
			if (___hoveredCard instanceof SlimeSlasher) {
				for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
					if (SlimeSlasherAction.isAllySlime(m)) {
						m.renderReticle(sb);
					}
				}
				return SpireReturn.Continue();
			} else if (___hoveredCard instanceof TargetAllyCard) {
				if (___hoveredCard.target == AbstractCard.CardTarget.ENEMY) {
					if (hoveredally != null) {
						hoveredally.renderReticle(sb);
					}
					return SpireReturn.Return();
				} else {
					for (AbstractCreature c : BetterFriendlyMinionsUtils.getAllyList()) {
						if (c != AbstractDungeon.player) {
							c.renderReticle(sb);
						}
					}
					return SpireReturn.Continue();
				}
			} else {
				return SpireReturn.Continue();
			}
		}
	}
}
