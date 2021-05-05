package TheCopycat.vfx;

import TheCopycat.utils.CopycatTargetArrow;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class CopycatFlashTargetArrowEffect extends AbstractGameEffect {
	private static final float DURATION = 1.4F;
	private static final float FADE = 0.3F;

	AbstractCreature from, to;
	float arrowTime;
	float alpha;
	float maxAlpha;

	public CopycatFlashTargetArrowEffect(AbstractCreature from, AbstractCreature to) {
		this(from, to, 1.0f);
	}

	public CopycatFlashTargetArrowEffect(AbstractCreature from, AbstractCreature to, float alpha) {
		this.from = from;
		this.to = to;
		arrowTime = 0;
		maxAlpha = alpha;
	}

	public void update() {
		alpha = maxAlpha;
		if (arrowTime < FADE) {
			alpha *= arrowTime / FADE;
		} else if (arrowTime > DURATION - FADE) {
			alpha *= (DURATION - arrowTime) / FADE;
		}
		arrowTime += Gdx.graphics.getDeltaTime();
		if (arrowTime >= DURATION) {
			isDone = true;
		}
	}

	public void render(SpriteBatch sb) {
		CopycatTargetArrow.drawTargetArrow(
				sb, from.hb, to.hb, CopycatTargetArrow.CONTROL_HEIGHT * Settings.scale, arrowTime, alpha, null);
	}

	public void dispose() {
	}
}
