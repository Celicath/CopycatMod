package TheCopycat.friendlyminions;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.AutoRetargetMinionAction;
import TheCopycat.powers.DoubleTimePower;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public abstract class AbstractCopycatMinion extends AbstractFriendlyMonster {
	public int index;
	public static ArrayList<AbstractPower> dummyPowers = new ArrayList<>();

	public boolean moveCountUpdate = true;
	int moveCount = 0;

	public static Color[] hpColors = new Color[]{
			new Color(0.05F, 0.8F, 0.05F, 0.0F),
			new Color(0.2F, 0.2F, 0.9F, 0.0F),
			new Color(0.75F, 0.05F, 0.75F, 0.0F),
			new Color(0.05F, 0.7F, 0.7F, 0.0F),
			new Color(0.55F, 0.6F, 0.5F, 0.0F)
	};

	public static Color[] arrowColors = new Color[]{
			new Color(0.5F, 0.9F, 0.5F, 0.5F),
			new Color(0.6F, 0.6F, 0.95F, 0.5F),
			new Color(0.85F, 0.5F, 0.85F, 0.5F),
			new Color(0.5F, 0.8F, 0.8F, 0.5F),
			new Color(0.75F, 0.8F, 0.7F, 0.5F)
	};

	public static Texture[][] attackIntentTextures;

	static {
		attackIntentTextures = new Texture[5][7];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 7; j++) {
				attackIntentTextures[i][j] = ImageMaster.loadImage(CopycatModMain.makePath("ui/intent/" + i + "/attack_intent_" + (j + 1) + ".png"));
			}
		}
	}

	@Override
	public Texture[] getAttackIntents() {
		return attackIntentTextures[index];
	}

	public AbstractCopycatMinion(String name, String id, int maxHealth, float width, float height, String imgUrl) {
		super(name, id, maxHealth, 0, 0, width, height, imgUrl, 0, 0);
	}

	static float[] offsetXPos = new float[]{-280.0f, -160.0f, 150.0f, 250.0f};
	static float[] offsetYPos = new float[]{100.0f, 300.0f, 300.0f, 100.0f};

	public static float calcXPos(int index) {
		return AbstractDungeon.player.drawX + offsetXPos[index] * Settings.xScale;
	}

	public static float calcYPos(int index) {
		return AbstractDungeon.floorY + offsetYPos[index] * Settings.yScale;
	}

	public void setIndex(int index) {
		this.index = index;
		drawX = calcXPos(index);
		drawY = calcYPos(index);
		refreshHitboxLocation();
		refreshIntentHbLocation();
	}

	public abstract void doMove(AbstractMonster target);

	public void clearPowers() {
		powers.clear();
	}

	@Override
	public void die() {
		super.die();
		for (AbstractPower p : powers) {
			p.onRemove();
		}
		clearPowers();
		isDead = true;
		if (index < BetterFriendlyMinionsUtils.copycatMinions.length) {
			BetterFriendlyMinionsUtils.copycatMinions[index] = null;
		}
		addToBot(new AutoRetargetMinionAction());
	}

	@Override
	public void render(SpriteBatch sb) {
		Color redColor = ReflectionHacks.getPrivate(this, AbstractCreature.class, "redHbBarColor");
		hpColors[index].a = redColor.a;
		ReflectionHacks.setPrivate(this, AbstractCreature.class, "redHbBarColor", hpColors[index]);
		super.render(sb);
		ReflectionHacks.setPrivate(this, AbstractCreature.class, "redHbBarColor", redColor);
	}

	public int getMoveCount() {
		if (moveCountUpdate) {
			AbstractPower pow = AbstractDungeon.player.getPower(DoubleTimePower.POWER_ID);
			boolean additional = pow != null;
			if (additional) {
				for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
					if (!mo.isDeadOrEscaped() && MonsterHelper.getTarget(mo) == this) {
						additional = false;
						break;
					}
				}
			}
			moveCount = 1 + (additional ? pow.amount : 0);
			moveCountUpdate = false;
		}
		return moveCount;
	}
}
