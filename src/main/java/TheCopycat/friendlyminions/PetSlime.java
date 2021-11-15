package TheCopycat.friendlyminions;

import TheCopycat.CopycatModMain;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateFastAttackAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.exordium.ApologySlime;

public class PetSlime extends ApologySlime {
	private static final String RAW_ID = "PetSlime";
	private static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final String NAME = CardCrawlGame.languagePack.getMonsterStrings(ID).NAME;
	Color tintColor;

	public PetSlime(Color color) {
		name = NAME;
		tintColor = color;
	}

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

	@Override
	public void render(SpriteBatch sb) {
		if (tintColor != null) {
			Color prevColor = tint.color;
			tint.color = tintColor.cpy().mul(tint.color);
			super.render(sb);
			tint.color = prevColor;
		} else {
			super.render(sb);
		}
	}
}
