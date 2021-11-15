package TheCopycat.relics;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SummonCopycatMinionAction;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.friendlyminions.Replica;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class SummoningMirror extends CustomRelic {

	private static final String RAW_ID = "SummoningMirror";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final String IMG = CopycatModMain.GetRelicPath(RAW_ID);
	public static final String OUTLINE = CopycatModMain.GetRelicOutlinePath(RAW_ID);

	public SummoningMirror() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.UNCOMMON, LandingSound.FLAT);
	}

	@Override
	public void atBattleStart() {
		flash();
		addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
		AbstractRelic relic = this;
		if (AbstractDungeon.player.relics.stream().anyMatch(r -> !(r instanceof SummoningMirror))) {
			do {
				relic = AbstractDungeon.player.relics.get(AbstractDungeon.cardRandomRng.random(0, AbstractDungeon.player.relics.size() - 1));
			} while(relic instanceof SummoningMirror);
		}

		Replica replica = new Replica(relic, AbstractDungeon.player.relics.size());
		addToBot(new SummonCopycatMinionAction(replica));
		grayscale = true;
	}

	@Override
	public void justEnteredRoom(AbstractRoom room) {
		grayscale = false;
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new SummoningMirror();
	}
}
