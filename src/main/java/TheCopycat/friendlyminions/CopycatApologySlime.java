package TheCopycat.friendlyminions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.exordium.ApologySlime;

public class CopycatApologySlime extends ApologySlime {
	@Override
	protected void getMove(int num) {
		this.setMove((byte) 1, Intent.ATTACK, damage.get(0).base);
	}

	@Override
	public void takeTurn() {
		AbstractDungeon.actionManager.addToBottom(new AnimateFastAttackAction(this));
		AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, damage.get(0), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
		setMove((byte) 1, Intent.ATTACK, damage.get(0).base);
	}
}
