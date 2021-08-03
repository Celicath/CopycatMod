package TheCopycat.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.HashSet;

public class TextEffect extends AbstractGameEffect {
	public static HashSet<Integer> messageIndexSet = new HashSet<>();
	public static HashSet<Integer> positionSet = new HashSet<>();
	private String message;
	private float x;
	private float y;
	private int messageIndex;
	private int position;

	public TextEffect(float x, float y, int messageIndex, String message) {
		this.message = message;
		this.x = x;
		for (position = 1; positionSet.contains(position); position++) ;
		this.y = y + 100.0F * position * Settings.scale;
		this.messageIndex = messageIndex;
		color = Color.WHITE.cpy();
		duration = 1.0F;

		messageIndexSet.add(messageIndex);
		positionSet.add(position);
	}

	public void update() {
		color.a = Interpolation.pow5Out.apply(0.0F, 0.8F, this.duration);
		color.a = MathUtils.clamp(color.a + MathUtils.random(-0.05F, 0.05F), 0.0F, 1.0F);
		duration -= Gdx.graphics.getDeltaTime();

		if (duration < 0.0F) {
			isDone = true;
			messageIndexSet.remove(messageIndex);
			positionSet.remove(position);
		}
	}

	public void render(SpriteBatch sb) {
		FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, message, x, y, color, 2.0F - duration / 5.0F + MathUtils.random(0.04F));
		sb.setBlendFunction(770, 1);
		FontHelper.renderFontCentered(sb, FontHelper.SCP_cardTitleFont_small, message, x, y, color, 0.05F + (2.0F - duration / 5.0F) + MathUtils.random(0.04F));
		sb.setBlendFunction(770, 771);
	}

	public void dispose() {
	}
}
