package TheCopycat.utils;

import com.megacrit.cardcrawl.core.AbstractCreature;

public class BaseGamePrivateBusterUtils {
	public static void refreshHitboxLocation(AbstractCreature c) {
		c.hb.move(c.drawX + c.hb_x + c.animX, c.drawY + c.hb_y + c.hb_h / 2.0F);
		c.healthHb.move(c.hb.cX, c.hb.cY - c.hb_h / 2.0F - c.healthHb.height / 2.0F);
	}
}
