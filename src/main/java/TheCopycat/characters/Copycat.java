package TheCopycat.characters;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.Defend;
import TheCopycat.cards.Mimic;
import TheCopycat.cards.Strike;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.relics.LuckyBag;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class Copycat extends CustomPlayer {
	public static final CharacterStrings charStrings = CardCrawlGame.languagePack.getCharacterString(CopycatModMain.makeID("TheCopycat"));
	public static final Logger logger = LogManager.getLogger(CopycatModMain.class.getName());

	public static final int ENERGY_PER_TURN = 3;
	public static final int STARTING_HP = 88;
	public static final int MAX_HP = 88;
	public static final int STARTING_GOLD = 99;
	public static final int CARD_DRAW = 5;
	public static final int ORB_SLOTS = 0;

	public static final String[] orbTextures = {
		"CopycatMod/images/char/TheCopycat/orb/layer1.png",
		"CopycatMod/images/char/TheCopycat/orb/layer2.png",
		"CopycatMod/images/char/TheCopycat/orb/layer3.png",
		"CopycatMod/images/char/TheCopycat/orb/layer4.png",
		"CopycatMod/images/char/TheCopycat/orb/layer5.png",
		"CopycatMod/images/char/TheCopycat/orb/layer6.png",
		"CopycatMod/images/char/TheCopycat/orb/layer1d.png",
		"CopycatMod/images/char/TheCopycat/orb/layer2d.png",
		"CopycatMod/images/char/TheCopycat/orb/layer3d.png",
		"CopycatMod/images/char/TheCopycat/orb/layer4d.png",
		"CopycatMod/images/char/TheCopycat/orb/layer5d.png",};

	public Copycat(String name, PlayerClass setClass) {
		super(name, setClass, orbTextures,
			"CopycatMod/images/char/TheCopycat/orb/vfx.png", null, null, null);

		CharSelectInfo loadout = getLoadout();
		initializeClass(CopycatModMain.makePath("char/TheCopycat/idle.png"),
			CopycatModMain.makePath(CopycatModMain.THE_COPYCAT_SHOULDER_1),
			CopycatModMain.makePath(CopycatModMain.THE_COPYCAT_SHOULDER_2),
			CopycatModMain.makePath(CopycatModMain.THE_COPYCAT_CORPSE),
			loadout, 0.0F, 0.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));

		this.dialogX = (this.drawX + 0.0F * Settings.scale);
		this.dialogY = (this.drawY + 220.0F * Settings.scale);
	}

	@Override
	public CharSelectInfo getLoadout() {
		return new CharSelectInfo(
			getLocalizedCharacterName(),
			charStrings.TEXT[0],
			STARTING_HP, MAX_HP, ORB_SLOTS, STARTING_GOLD, CARD_DRAW, this, getStartingRelics(),
			getStartingDeck(), false);
	}

	@Override
	public ArrayList<String> getStartingDeck() {
		ArrayList<String> retVal = new ArrayList<>();

		retVal.add(Strike.ID);
		retVal.add(Strike.ID);
		retVal.add(Strike.ID);
		retVal.add(Strike.ID);
		retVal.add(Defend.ID);
		retVal.add(Defend.ID);
		retVal.add(Defend.ID);
		retVal.add(Defend.ID);
		retVal.add(Mimic.ID);
		retVal.add(Mimic.ID);

		return retVal;
	}

	public ArrayList<String> getStartingRelics() {
		ArrayList<String> retVal = new ArrayList<>();

		retVal.add(LuckyBag.ID);

		UnlockTracker.markRelicAsSeen(LuckyBag.ID);

		return retVal;
	}

	@Override
	public void doCharSelectScreenSelectEffect() {
		CardCrawlGame.sound.playA("BUFF_2", 0.80f); // Sound Effect
		CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT,
			false); // Screen Effect
	}

	@Override
	public String getCustomModeCharacterButtonSoundKey() {
		return "BUFF_2";
	}

	@Override
	public int getAscensionMaxHPLoss() {
		return 6;
	}

	@Override
	public AbstractCard.CardColor getCardColor() {
		return CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	}

	@Override
	public Color getCardTrailColor() {
		return CopycatModMain.COPYCAT_BLUE.cpy();
	}

	@Override
	public BitmapFont getEnergyNumFont() {
		return FontHelper.energyNumFontRed;
	}

	@Override
	public String getLocalizedCharacterName() {
		return charStrings.NAMES[1];
	}

	@Override
	public AbstractCard getStartCardForEvent() {
		return new Mimic();
	}

	@Override
	public String getTitle(AbstractPlayer.PlayerClass playerClass) {
		return charStrings.NAMES[0];
	}

	@Override
	public AbstractPlayer newInstance() {
		return new Copycat(this.name, this.chosenClass);
	}

	@Override
	public Color getCardRenderColor() {
		return CopycatModMain.COPYCAT_BLUE.cpy();
	}

	@Override
	public Color getSlashAttackColor() {
		return CopycatModMain.COPYCAT_BLUE;
	}

	@Override
	public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
		return new AbstractGameAction.AttackEffect[]{
			AbstractGameAction.AttackEffect.FIRE,
			AbstractGameAction.AttackEffect.FIRE,
			AbstractGameAction.AttackEffect.FIRE,
			AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
			AbstractGameAction.AttackEffect.SLASH_HEAVY,
			AbstractGameAction.AttackEffect.BLUNT_LIGHT,
			AbstractGameAction.AttackEffect.SLASH_HEAVY};
	}

	@Override
	public String getSpireHeartText() {
		return CardCrawlGame.languagePack.getEventString(CopycatModMain.makeID("SpireHeart")).DESCRIPTIONS[0];
	}

	@Override
	public String getVampireText() {
		return CardCrawlGame.languagePack.getEventString(CopycatModMain.makeID("Vampires")).DESCRIPTIONS[0];
	}
}
