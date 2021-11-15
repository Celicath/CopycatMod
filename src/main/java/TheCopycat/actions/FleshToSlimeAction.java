package TheCopycat.actions;

import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.friendlyminions.PetSlime;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;

public class FleshToSlimeAction extends AbstractGameAction {
	private final DamageInfo info;

	public FleshToSlimeAction(AbstractCreature target, DamageInfo info, AttackEffect effect) {
		this.info = info;
		this.setValues(target, info);
		this.actionType = ActionType.DAMAGE;
		this.attackEffect = effect;
		duration = startDuration = Settings.ACTION_DUR_XFAST;
	}

	public void update() {
		if (this.duration == startDuration) {
			AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, attackEffect));
			int index = BetterFriendlyMinionsUtils.getNextCopycatMinionSlot();
			if (index != -1) {
				float x = AbstractDungeon.player.hb.cX;
				float y = AbstractDungeon.player.hb.cY;
				AbstractDungeon.player.hb.cX = AbstractCopycatMinion.calcXPos(index);
				AbstractDungeon.player.hb.cY = AbstractCopycatMinion.calcYPos(index);
				AbstractDungeon.effectList.add(new FlyingOrbEffect(target.hb.cX, target.hb.cY));
				AbstractDungeon.player.hb.cX = x;
				AbstractDungeon.player.hb.cY = y;
			}
		}

		tickDuration();
		if (isDone) {
			target.damage(info);
			if (target.lastDamageTaken > 0) {
				PetSlime slime = new PetSlime(null);
				addToBot(new SummonCopycatMinionAction(new MirrorMinion(slime.name, slime, target.lastDamageTaken)));
				addToTop(new WaitAction(0.1F));
			}

			if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
				AbstractDungeon.actionManager.clearPostCombatActions();
			}
		}
	}
}
