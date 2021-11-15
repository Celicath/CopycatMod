package TheCopycat.blights;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.monster.AbstractMonsterCard;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.BlightStrings;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;
import java.util.List;

public class Spiredex extends AbstractBlight implements CustomSavable<ArrayList<String>> {

	private static final String RAW_ID = "Spiredex";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final BlightStrings blightStrings = CardCrawlGame.languagePack.getBlightString(ID);
	private static final String NAME = blightStrings.NAME;
	private static final String[] DESCRIPTION = blightStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetBlightPath(RAW_ID);
	public static final String OUTLINE_IMG = CopycatModMain.GetBlightOutlinePath(RAW_ID);

	public static Random monsterCardRng = new Random();
	public static ArrayList<String> monsterCardRewardSaveData = new ArrayList<>();
	public static int saveDataIndex = 0;

	public Spiredex() {
		this(1);
	}

	public Spiredex(int amount) {
		super(ID, NAME, DESCRIPTION[0], IMG, true);
		counter = amount;
		img = ImageMaster.loadImage(IMG);
		outlineImg = ImageMaster.loadImage(OUTLINE_IMG);
		BaseMod.addSaveField(ID, this);
		updateDescription();
	}

	@Override
	public void updateDescription() {
		int count = Math.abs(counter);
		if (count == 1) {
			description = DESCRIPTION[0];
		} else if (count < 9) {
			description = DESCRIPTION[1] + count + DESCRIPTION[2];
		} else {
			description = DESCRIPTION[3];
		}
		tips.clear();
		tips.add(new PowerTip(name, description));
		tips.add(new PowerTip(
			BaseMod.getKeywordTitle(CopycatModMain.makeLowerID("monster_card")),
			BaseMod.getKeywordDescription(CopycatModMain.makeLowerID("monster_card"))));
		initializeTips();
	}

	public void replaceCardRewards(List<AbstractMonsterCard> monsterCards, ArrayList<AbstractCard> __result, float cardUpgradedChance) {
		if (!monsterCards.isEmpty()) {
			for (int i = 0; i < Math.abs(counter); i++) {
				if (i >= __result.size() || monsterCards.isEmpty()) {
					return;
				}

				AbstractCard c;
				int rerollRatio;
				int index;
				do {
					index = Spiredex.monsterCardRng.random(monsterCards.size() - 1);
					c = monsterCards.get(index);
					if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
						rerollRatio = c.rarity == AbstractCard.CardRarity.COMMON ? 4 : c.rarity == AbstractCard.CardRarity.UNCOMMON ? 3 : c.rarity == AbstractCard.CardRarity.RARE ? 0 : 6;
					} else if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite) {
						rerollRatio = 0;
					} else {
						rerollRatio = c.rarity == AbstractCard.CardRarity.COMMON ? 0 : c.rarity == AbstractCard.CardRarity.UNCOMMON ? 3 : c.rarity == AbstractCard.CardRarity.RARE ? 4 : 6;
					}
				} while (Spiredex.monsterCardRng.random(5) < rerollRatio);

				monsterCards.remove(index);
				c.resetAttributes();
				c = c.makeCopy();

				if (Spiredex.monsterCardRng.randomBoolean(cardUpgradedChance) && c.canUpgrade()) {
					c.upgrade();
				} else {
					for (AbstractRelic r : AbstractDungeon.player.relics) {
						r.onPreviewObtainCard(c);
					}
				}
				__result.set(0, c);
				monsterCardRewardSaveData.add(c.getMetricID());
				saveDataIndex++;
			}
		} else if (monsterCardRewardSaveData.size() > saveDataIndex) {
			for (int i = 0; i < Math.abs(counter); i++) {
				if (i >= __result.size()) {
					return;
				}
				String card = monsterCardRewardSaveData.get(saveDataIndex);
				__result.set(0, AbstractMonsterCard.createFromMetricID(card));
				saveDataIndex++;
			}
		}
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
		} else {
			monsterCardRewardSaveData.clear();
		}
	}
}
