package TheCopycat.friendlyminions;

import TheCopycat.CopycatModMain;
import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.HashMap;

public class Replica extends AbstractCopycatMinion {
	private static final String RAW_ID = "Replica";
	private static final String ID = CopycatModMain.makeID(RAW_ID);

	public static HashMap<AbstractRelic, Replica> relicMap = new HashMap<>();
	public AbstractRelic relic;
	public float relicRotation;

	public Replica(AbstractRelic relic, int hp) {
		super(relic.name, ID, hp, 128, 128, CopycatModMain.GetMinionPath(RAW_ID));
		this.relic = relic.makeCopy();
		this.relic.counter = relic.counter;
		this.relic.updateDescription(AbstractDungeon.player.chosenClass);
		relicMap.put(this.relic, this);
		setMove((byte) 1, Intent.DEBUG);
	}

	public static void resetRelics() {
		if (AbstractDungeon.player != null && AbstractDungeon.player.relics != null) {
			AbstractDungeon.player.relics.removeAll(relicMap.keySet());
		}
		relicMap.clear();
	}

	@Override
	public void doMove(AbstractMonster target) {
		// do nothing
	}

	@Override
	public void die() {
		super.die();
		AbstractDungeon.player.relics.remove(relic);
		relicMap.remove(relic);
	}

	@Override
	public void update() {
		super.update();
		relicRotation -= 144.0f * Gdx.graphics.getDeltaTime();
	}

	/*
	@Override
	protected void getMove(int num) {
		this.setMove((byte) 0, Intent.DEBUG);
	}

	@Override
	public void takeTurn() {
		// do nothing
	}


	@Override
	public void render(SpriteBatch sb) {
	}
	*/
}
