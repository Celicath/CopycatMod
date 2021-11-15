package TheCopycat.relics;

import TheCopycat.CopycatModMain;
import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class EnergyBag extends CustomRelic {

	private static final String RAW_ID = "EnergyBag";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final String IMG = CopycatModMain.GetRelicPath(RAW_ID);
	public static final String OUTLINE = CopycatModMain.GetRelicOutlinePath(RAW_ID);

	public EnergyBag() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.BOSS, LandingSound.FLAT);
	}

	@Override
	public void obtain() {
		for (int i = 0; i < AbstractDungeon.player.relics.size(); ++i) {
			if (AbstractDungeon.player.relics.get(i).relicId.equals(LuckyBag.ID)) {
				instantObtain(AbstractDungeon.player, i, true);
				return;
			}
		}
		super.obtain();
	}

	@Override
	public boolean canSpawn() {
		return AbstractDungeon.player.hasRelic(LuckyBag.ID);
	}

	@Override
	public void onEquip() {
		++AbstractDungeon.player.energy.energyMaster;
	}

	@Override
	public void onUnequip() {
		--AbstractDungeon.player.energy.energyMaster;
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	@Override
	public AbstractRelic makeCopy() {
		return new EnergyBag();
	}
}
