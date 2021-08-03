package TheCopycat.patches;

import TheCopycat.actions.SwitchSubstituteAction;
import TheCopycat.friendlyminions.SubstituteMinion;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

public class SubstitutePatch {
	@SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
	public static class StartOfTurnSwitch {
		@SpirePrefixPatch
		public static void Prefix() {
			if (SubstituteMinion.instance.isFront) {
				AbstractDungeon.actionManager.addToTop(new SwitchSubstituteAction(false));
			}
		}
	}

	@SpirePatch2(clz = AbstractMonster.class, method = "calculateDamage")
	public static class CalculateDamagePatch {
		@SpirePrefixPatch
		public static SpireReturn<Void> Prefix(AbstractMonster __instance, int dmg, @ByRef int[] ___intentDmg) {
			AbstractFriendlyMonster target = MonsterHelper.getTarget(__instance);
			if (target == SubstituteMinion.instance) {
				float tmp = dmg;
				for (AbstractPower p : __instance.powers) {
					tmp = p.atDamageGive(tmp, DamageInfo.DamageType.NORMAL);
				}
				for (AbstractPower p : target.powers) {
					tmp = p.atDamageReceive(tmp, DamageInfo.DamageType.NORMAL);
				}
				for (AbstractPower p : __instance.powers) {
					tmp = p.atDamageFinalGive(tmp, DamageInfo.DamageType.NORMAL);
				}
				for (AbstractPower p : target.powers) {
					tmp = p.atDamageFinalReceive(tmp, DamageInfo.DamageType.NORMAL);
				}

				dmg = MathUtils.floor(tmp);
				if (dmg < 0) dmg = 0;
				___intentDmg[0] = dmg;
				return SpireReturn.Return();
			} else {
				return SpireReturn.Continue();
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "render")
	public static class RenderPatch {
		@SpirePrefixPatch
		public static void Prefix(SpriteBatch sb) {
			if (!SubstituteMinion.instance.isDead && !SubstituteMinion.instance.isFront) {
				if (AbstractDungeon.getCurrRoom().monsters != null) {
					SubstituteMinion.instance.render(sb);
				}
			}
		}

		@SpirePostfixPatch
		public static void Postfix(SpriteBatch sb) {
			if (!SubstituteMinion.instance.isDead && SubstituteMinion.instance.isFront) {
				if (AbstractDungeon.getCurrRoom().monsters != null) {
					SubstituteMinion.instance.render(sb);
				}
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "update")
	public static class UpdatePatch {
		@SpirePostfixPatch
		public static void Postfix() {
			if (!SubstituteMinion.instance.isDead) {
				if (AbstractDungeon.getCurrRoom().monsters != null) {
					SubstituteMinion.instance.update();
					SubstituteMinion.instance.hb.update();
				}
			}
		}
	}

	@SpirePatch2(clz = AbstractCreature.class, method = "updatePowers")
	public static class UpdatePowersPatch {
		public static void Postfix(AbstractCreature __instance) {
			if (__instance == AbstractDungeon.player && !SubstituteMinion.instance.isDead) {
				SubstituteMinion.instance.updatePowers();
			}
		}
	}

	@SpirePatch2(clz = AbstractCreature.class, method = "applyEndOfTurnTriggers")
	public static class EndOfTurnPatch {
		public static void Postfix(AbstractCreature __instance) {
			if (__instance == AbstractDungeon.player && !SubstituteMinion.instance.isDead) {
				SubstituteMinion.instance.applyEndOfTurnTriggers();
				SubstituteMinion.instance.powers.forEach(AbstractPower::atEndOfRound);
			}
		}
	}

	@SpirePatch2(clz = MonsterHelper.class, method = "switchTarget")
	public static class SubstituteRedirectPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractMonster monster, AbstractFriendlyMonster newTarget) {
			if (!(monster instanceof AbstractFriendlyMonster) && !SubstituteMinion.instance.isDead && newTarget == null) {
				MonsterHelper.setTarget(monster, SubstituteMinion.instance);
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "movePosition")
	@SpirePatch2(clz = CustomPlayer.class, method = "movePosition")
	public static class ShieldAndSpearPositionPatch {
		@SpirePostfixPatch
		public static void Postfix() {
			SubstituteMinion.instance.resetPosition();
		}
	}
}
