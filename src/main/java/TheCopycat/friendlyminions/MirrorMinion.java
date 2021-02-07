package TheCopycat.friendlyminions;

import TheCopycat.CopycatModMain;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

public class MirrorMinion extends AbstractFriendlyMonster {
	private static String ID = CopycatModMain.makeID("MirrorMinion");
	AbstractMonster targetMonster;

	public MirrorMinion(String name, AbstractMonster targetMonster, int maxHealth, float offsetX, float offsetY) {
		super(name, ID, maxHealth, targetMonster.hb_x, targetMonster.hb_y, targetMonster.hb_w, targetMonster.hb_h, null, offsetX, offsetY);

		targetMonster.drawX = drawX;
		targetMonster.drawY = drawY;
		targetMonster.flipHorizontal = true;

		targetMonster.rollMove();
		targetMonster.createIntent();
	}

	@Override
	public void render(SpriteBatch sb) {
		targetMonster.render(sb);
	}
}
