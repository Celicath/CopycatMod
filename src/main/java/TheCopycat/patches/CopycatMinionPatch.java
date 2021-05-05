package TheCopycat.patches;

import TheCopycat.cards.MeFirst;
import TheCopycat.crossovers.BetterFriendlyMinions;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.vfx.CopycatFlashTargetArrowEffect;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.IntentFlashAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ShowMoveNameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import kobting.friendlyminions.helpers.MinionConfigHelper;
import kobting.friendlyminions.patches.MonsterSetMovePatch;

import java.util.ArrayList;

public class CopycatMinionPatch {
	public static float noChangeMagicNumber = -320.0f;
	static float tempChance = noChangeMagicNumber;

	public static float calcMinionTargetChance() {
		ArrayList<AbstractMonster> list = BetterFriendlyMinions.getMinionList();
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
		@SpirePostfixPatch
		public static void Postfix(AbstractCreature __instance) {
			if (__instance instanceof AbstractPlayer) {
				BetterFriendlyMinions.getMinionList().forEach(m -> {
					if (m instanceof AbstractCopycatMinion) {
						AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
							@Override
							public void update() {
								AbstractMonster intentMonster = m instanceof MirrorMinion ? ((MirrorMinion) m).origMonster : m;
								AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, BetterFriendlyMinions.minionAiRng);

								((AbstractCopycatMinion) m).doMove(target);
								if (intentMonster.intent != AbstractMonster.Intent.NONE) {
									AbstractDungeon.actionManager.addToTop(new IntentFlashAction(intentMonster));
									AbstractDungeon.actionManager.addToTop(new ShowMoveNameAction(intentMonster));
									if (MeFirst.checkActivate(intentMonster, 1) || MeFirst.checkActivate(intentMonster, 3)) {
										AbstractDungeon.actionManager.addToTop(new VFXAction(new CopycatFlashTargetArrowEffect(m, target, 0.75f), 0.2f));
									}
								}
								isDone = true;
							}
						});
					}
				});
			}
		}
	}

	@SpirePatch2(clz = MonsterGroup.class, method = "showIntent")
	public static class ShowIntentPatch {
		@SpirePostfixPatch
		public static void Postfix() {
			BetterFriendlyMinions.getMinionList().forEach(m -> {
				if (m instanceof AbstractCopycatMinion) {
					AbstractMonster intentMonster = m instanceof MirrorMinion ? ((MirrorMinion) m).origMonster : m;
					intentMonster.createIntent();
				}
			});
		}
	}

	@SpirePatch2(clz = MonsterSetMovePatch.class, method = "maybeChangeIntent")
	public static class AdjustChangeIntentPatch {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(AbstractMonster monster) {
			if (monster instanceof AbstractCopycatMinion || MirrorMinion.minionCreating) {
				return SpireReturn.Return();
			}
			for (int i = 0; i < 4; i++) {
				if (BetterFriendlyMinions.copycatMinions[i] instanceof MirrorMinion && ((MirrorMinion) BetterFriendlyMinions.copycatMinions[i]).origMonster == monster) {
					return SpireReturn.Return();
				}
			}

			float newChance = calcMinionTargetChance();
			if (newChance > MinionConfigHelper.MinionAttackTargetChance) {
				tempChance = MinionConfigHelper.MinionAttackTargetChance;
				MinionConfigHelper.MinionAttackTargetChance = newChance;
			}

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
}
