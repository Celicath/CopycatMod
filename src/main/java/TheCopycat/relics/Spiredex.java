package TheCopycat.relics;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.monster.AbstractMonsterCard;
import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public class Spiredex extends CustomRelic implements CustomSavable<ArrayList<String>> {

	private static final String RAW_ID = "Spiredex";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final String IMG = CopycatModMain.GetRelicPath(RAW_ID);
	public static final String OUTLINE = CopycatModMain.GetRelicOutlinePath(RAW_ID);

	public ArrayList<String> monsterCardRewardSaveData = new ArrayList<>();
	int saveDataIndex = 0;

	public Spiredex() {
		super(ID, new Texture(IMG), new Texture(OUTLINE), RelicTier.STARTER, LandingSound.MAGICAL);
	}

	@Override
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void addCardInReward(ArrayList<AbstractCard> monsterCards, ArrayList<AbstractCard> __result, float cardUpgradedChance) {
		if (!monsterCards.isEmpty()) {
			int index = AbstractDungeon.miscRng.random(monsterCards.size() - 1);
			AbstractCard c = monsterCards.get(index);
			c.resetAttributes();
			if (AbstractDungeon.miscRng.randomBoolean(cardUpgradedChance) && c.canUpgrade()) {
				c.upgrade();
			} else {
				for (AbstractRelic r : AbstractDungeon.player.relics) {
					r.onPreviewObtainCard(c);
				}
			}
			__result.add(c);
			monsterCardRewardSaveData.add(c.getMetricID());
			monsterCards.remove(index);
		} else if (monsterCardRewardSaveData.size() > saveDataIndex) {
			AbstractDungeon.miscRng.random(3);
			AbstractDungeon.miscRng.random(3);
			String card = monsterCardRewardSaveData.get(saveDataIndex);
			__result.add(AbstractMonsterCard.createFromMetricID(card));
			saveDataIndex++;
		}
	}

	@Override
	public void onEnterRoom(AbstractRoom room) {
		monsterCardRewardSaveData.clear();
	}

	@Override
	public AbstractRelic makeCopy() {
		return new Spiredex();
	}

	@Override
	public ArrayList<String> onSave() {
		return monsterCardRewardSaveData;
	}

	@Override
	public void onLoad(ArrayList<String> data) {
		if (data != null) {
			monsterCardRewardSaveData = data;
			saveDataIndex = 0;
		}
	}
}
