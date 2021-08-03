package TheCopycat.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.vfx.combat.PotionBounceEffect;

public class BowlingFlaskAction extends AbstractGameAction {
	private static final float DURATION = 0.01F;
	private static final float POST_ATTACK_WAIT_DUR = 0.1F;
	private int numTimes;
	private int amount;
	private AbstractCreature effectFrom;

	public BowlingFlaskAction(AbstractCreature target, int amount, int numTimes, AbstractCreature effectFrom) {
		this.target = target;
		this.actionType = ActionType.DEBUFF;
		this.duration = DURATION;
		this.numTimes = numTimes;
		this.amount = amount;
		this.effectFrom = effectFrom;
	}

	public void update() {
		if (target == null) {
			isDone = true;
		} else if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
			AbstractDungeon.actionManager.clearPostCombatActions();
			isDone = true;
		} else {
			if (numTimes > 1) {
				--numTimes;
				addToTop(new BowlingFlaskAction(target, amount, numTimes, target));
			}
			if (target.currentHealth > 0) {
				addToTop(new ApplyPowerAction(target, AbstractDungeon.player, new PoisonPower(target, AbstractDungeon.player, amount), amount, true, AttackEffect.POISON));
				addToTop(new WaitAction(POST_ATTACK_WAIT_DUR));
			}

			Settings.scale *= 0.75f;
			PotionBounceEffect effect = new PotionBounceEffect(effectFrom.hb.cX, effectFrom.hb.cY, target.hb.cX, target.hb.cY) {
				int counter = 0;

				@Override
				public void update() {
					super.update();
					if (++counter >= 5) {
						counter = 0;
						super.update();
					}
				}
			};
			Settings.scale /= 0.75f;
			addToTop(new VFXAction(effect, 0.3f));

			isDone = true;
		}
	}
}
