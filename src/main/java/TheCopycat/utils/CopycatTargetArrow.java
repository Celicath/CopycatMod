package TheCopycat.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

public class CopycatTargetArrow {
	public static final float CONTROL_HEIGHT = 200.0f;
	public static ShapeRenderer renderer = new ShapeRenderer();
	public static Color pink = new Color(1.0f, 0.7f, 0.8f, 0.5f);
	public static Color white = new Color(1.0f, 1.0f, 1.0f, 0.9f);

	public static Color getColor(float t, float timer, Color color, float alpha) {
		float x = Math.abs((timer * 1.5f + 0.4f) % 2f - 0.5f - t) / 0.08f;
		if (x > 1) {
			Color c = color.cpy();
			c.a *= alpha;
			return c;
		} else {
			x = 1 - (1 - x) * (1 - x);
			return new Color(
				MathUtils.lerp(white.r, color.r, x),
				MathUtils.lerp(white.g, color.g, x),
				MathUtils.lerp(white.b, color.b, x),
				MathUtils.lerp(white.a, color.a, x) * alpha);
		}
	}

	public static void drawTargetArrow(SpriteBatch sb, Hitbox h1, Hitbox h2, float vDist, float timer, float alpha, Color color) {
		if (h1.cX == h2.cX && h1.cY == h2.cY) {
			return;
		}

		if (color == null) {
			color = pink;
		}

		sb.end();

		Vector2 start = new Vector2(h1.cX, h1.cY);
		Vector2 end = new Vector2(h2.cX, h2.cY);
		Vector2 control = new Vector2((h1.cX + h2.cX) / 2, (h1.cY + h2.cY) / 2 + vDist);

		Vector2 nstart = new Vector2(start.y - control.y, control.x - start.x);
		Vector2 nend = new Vector2(control.y - end.y, end.x - control.x);

		Vector2 tmp = end.cpy();
		int segs = (int) (tmp.sub(start).len() / Settings.scale / 10.0f);
		if (segs < 2) segs = 2;

		Vector2 v1 = start.cpy();
		Vector2 v2 = new Vector2();
		Color c1 = getColor(0, timer, color, alpha);
		Vector2 n1 = nstart.cpy().nor().scl(10.0f * Settings.scale);

		boolean blendDisabled = false;
		if (!Gdx.gl.glIsEnabled(GL20.GL_BLEND)) {
			blendDisabled = true;
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		renderer.setProjectionMatrix(sb.getProjectionMatrix().cpy());
		renderer.begin(ShapeRenderer.ShapeType.Filled);

		renderer.setColor(Color.WHITE.cpy());
		for (int i = 0; i < segs - 1; i++) {
			float t2 = (float) (i + 1) / segs;
			Bezier.quadratic(v2, t2, start, control, end, tmp);
			Color c2 = getColor(t2, timer, color, alpha);
			tmp.set(MathUtils.lerp(nstart.x, nend.x, t2), MathUtils.lerp(nstart.y, nend.y, t2)).nor().scl(10.0f * Settings.scale);

			if (i == 0) {
				renderer.triangle(
					v1.x - n1.x, v1.y - n1.y, v2.x - tmp.x, v2.y - tmp.y, v2.x, v2.y,
					c1, c2, c2);
				renderer.triangle(
					v1.x + n1.x, v1.y + n1.y, v2.x, v2.y, v2.x + tmp.x, v2.y + tmp.y,
					c1, c2, c2);
			} else {
				renderer.triangle(
					v1.x - n1.x, v1.y - n1.y, v2.x - tmp.x, v2.y - tmp.y, v1.x + n1.x, v1.y + n1.y,
					c1, c2, c1);
				renderer.triangle(
					v2.x - tmp.x, v2.y - tmp.y, v2.x + tmp.x, v2.y + tmp.y, v1.x + n1.x, v1.y + n1.y,
					c2, c2, c1);
			}
			c1.set(c2);
			n1.set(tmp);
			v1.set(v2);
		}

		renderer.triangle(
			v2.x - n1.x, v2.y - n1.y, end.x, end.y, v2.x + n1.x, v2.y + n1.y,
			c1, getColor(1.0f, timer, color, alpha), c1);

		renderer.end();
		if (blendDisabled) {
			Gdx.gl.glDisable(GL20.GL_BLEND);
		}

		Bezier.quadratic(v2, 0.5f, start, control, end, tmp);

		sb.begin();
	}
}
