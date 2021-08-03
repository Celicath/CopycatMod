package TheCopycat.friendlyminions;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SwitchSubstituteAction;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SubstituteMinion extends AbstractCopycatMinion implements CustomSavable<int[]> {
	private static final String RAW_ID = "SubstituteMinion";
	private static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final String NAME = CardCrawlGame.languagePack.getMonsterStrings(ID).NAME;

	private static final float WIDTH = 160;
	private static final float HEIGHT = 160;

	static float offsetXPos = -135.0f;
	static float offsetYPos = 50.0f;

	public static SubstituteMinion instance;

	public boolean isFront;

	public SubstituteMinion() {
		super(NAME, ID, 0, WIDTH, HEIGHT, CopycatModMain.GetMinionPath(RAW_ID));

		resetPosition();

		isDead = true;
		index = 4;

		BaseMod.addSaveField(ID, this);
	}

	public void resetPosition() {
		if (AbstractDungeon.player != null) {
			drawX = AbstractDungeon.player.drawX + offsetXPos * Settings.xScale;
			drawY = AbstractDungeon.floorY + offsetYPos * Settings.yScale;
		}
		isFront = false;
	}

	public void summon(int hp, int mhp) {
		resetPosition();
		isDying = isDead = false;
		currentHealth = hp;
		maxHealth = mhp;
		tint.color.a = 0.0f;
		healthBarUpdatedEvent();
		showHealthBar();
		clearPowers();
	}

	@Override
	public void doMove(AbstractMonster target) {
	}

	@Override
	public int[] onSave() {
		if (isDead) return null;
		return new int[]{currentHealth, maxHealth};
	}

	@Override
	public void onLoad(int[] data) {
		if (data == null) {
			isDead = true;
		} else {
			summon(data[0], data[1]);
		}
		isFront = false;
	}

	@Override
	public void die() {
		super.die();
		addToTop(new SwitchSubstituteAction(false));
	}
}
