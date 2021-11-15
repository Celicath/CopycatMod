package TheCopycat.potions;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SummonCopycatMinionAction;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.friendlyminions.PetSlime;
import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class SlimePotion extends AbstractPotion {
	private static final String RAW_ID = "SlimePotion";
	public static final String POTION_ID = CopycatModMain.makeID(RAW_ID);
	private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(POTION_ID);
	public static final String NAME = potionStrings.NAME;
	public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;
	public static final int HP = 7;

	public static Texture CONTAINER = ImageMaster.loadImage(CopycatModMain.GetPotionPath("slime", "body"));
	public static Texture LIQUID = ImageMaster.loadImage(CopycatModMain.GetPotionPath("slime", "liquid"));
	public static Texture HYBRID = ImageMaster.loadImage(CopycatModMain.GetPotionPath("slime", "hybrid"));
	public static Texture SPOTS = ImageMaster.loadImage(CopycatModMain.GetPotionPath("slime", "spots"));
	public static Texture OUTLINE = ImageMaster.loadImage(CopycatModMain.GetPotionPath("slime", "outline"));

	public SlimePotion() {
		super(NAME, POTION_ID, PotionRarity.UNCOMMON, PotionSize.BOTTLE, PotionEffect.NONE, Color.GREEN.cpy(), Color.CYAN.cpy(), Color.BLUE.cpy());
		isThrown = false;
	}

	@Override
	public void initializeData() {
		potency = getPotency();
		description = DESCRIPTIONS[0] + potency + DESCRIPTIONS[1] + HP + DESCRIPTIONS[2];
		tips.clear();
		tips.add(new PowerTip(name, description));
		tips.add(new PowerTip(
			BaseMod.getKeywordTitle(CopycatModMain.makeLowerID("pet_slime")),
			BaseMod.getKeywordDescription(CopycatModMain.makeLowerID("pet_slime"))
		));
	}

	@Override
	public void use(AbstractCreature target) {
		for (int i = 0; i < potency; i++) {
			PetSlime slime = new PetSlime(null);
			MirrorMinion minion = new MirrorMinion(slime.name, slime, HP);
			addToBot(new SummonCopycatMinionAction(minion));
		}
	}

	@Override
	public int getPotency(int ascensionLevel) {
		return 2;
	}

	@Override
	public AbstractPotion makeCopy() {
		return new SlimePotion();
	}
}
