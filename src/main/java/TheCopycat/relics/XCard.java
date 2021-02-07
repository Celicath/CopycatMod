package TheCopycat.relics;

import TheCopycat.CopycatModMain;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class XCard extends CustomRelic {

	private static final String RAW_ID = "XCard";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final String IMG = CopycatModMain.GetRelicPath(RAW_ID);
	public static final String OUTLINE = CopycatModMain.GetRelicOutlinePath(RAW_ID);

	public XCard() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.SPECIAL, LandingSound.FLAT);
	}

	@Override
	public int changeNumberOfCardsInReward(int numberOfCards) {
		return numberOfCards - 3;
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new XCard();
	}
}
