package TheCopycat;

import TheCopycat.actions.VenomologyActivateAction;
import TheCopycat.blights.Spiredex;
import TheCopycat.cards.Mimic;
import TheCopycat.cards.monster.AbstractMonsterCard;
import TheCopycat.cards.monster.Charging;
import TheCopycat.cards.monster.DynamicCard;
import TheCopycat.cards.monster.NoMove;
import TheCopycat.cards.temp.ChooseProtective;
import TheCopycat.characters.Copycat;
import TheCopycat.commands.RedirectCommand;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.patches.CaptureEnemyMovePatch;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.patches.TargetAllyPatch;
import TheCopycat.powers.VenomologyPower;
import TheCopycat.relics.LuckyBag;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import TheCopycat.vfx.TextEffect;
import basemod.AutoAdd;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.devcommands.ConsoleCommand;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.custom.CustomMod;

import java.nio.charset.StandardCharsets;
import java.util.List;

@SpireInitializer
public class CopycatModMain implements EditStringsSubscriber, EditKeywordsSubscriber, PostInitializeSubscriber,
		EditCharactersSubscriber, EditCardsSubscriber, EditRelicsSubscriber,
		PreStartGameSubscriber, OnStartBattleSubscriber, AddCustomModeModsSubscriber,
		PostCreateStartingDeckSubscriber {

	private static final String MODNAME = "The Copycat";
	private static final String AUTHOR = "Celicath";
	private static final String DESCRIPTION = "Properly Unlocks everything.";

	public static final String MOD_ID = "CopycatMod";
	private static final String COPYCAT_MOD_ASSETS_FOLDER = MOD_ID + "/images";

	public static final String BADGE_IMAGE = "Badge.png";

	public static final Color COPYCAT_BLUE = CardHelper.getColor(0x8C, 0xAE, 0xE6);

	// Card backgrounds
	private static final String ATTACK_COPYCAT_BLUE = "512/bg_attack_copycat_blue.png";
	private static final String POWER_COPYCAT_BLUE = "512/bg_power_copycat_blue.png";
	private static final String SKILL_COPYCAT_BLUE = "512/bg_skill_copycat_blue.png";
	private static final String ENERGY_ORB_COPYCAT_BLUE = "512/card_copycat_blue_orb.png";
	private static final String CARD_ENERGY_ORB = "512/card_small_orb.png";

	private static final String ATTACK_COPYCAT_BLUE_PORTRAIT = "1024/bg_attack_copycat_blue.png";
	private static final String POWER_COPYCAT_BLUE_PORTRAIT = "1024/bg_power_copycat_blue.png";
	private static final String SKILL_COPYCAT_BLUE_PORTRAIT = "1024/bg_skill_copycat_blue.png";
	private static final String ENERGY_ORB_COPYCAT_BLUE_PORTRAIT = "1024/card_copycat_blue_orb.png";

	// Character assets
	private static final String THE_COPYCAT_BUTTON = "charSelect/CopycatButton.png";
	private static final String THE_COPYCAT_PORTRAIT = "charSelect/CopycatPortraitBG.png";
	public static final String THE_COPYCAT_SHOULDER_1 = "char/TheCopycat/shoulder.png";
	public static final String THE_COPYCAT_SHOULDER_2 = "char/TheCopycat/shoulder2.png";
	public static final String THE_COPYCAT_CORPSE = "char/TheCopycat/corpse.png";

	// Crossovers
	public static boolean isDragonTamerLoaded;

	public static String makePath(String resource) {
		return COPYCAT_MOD_ASSETS_FOLDER + "/" + resource;
	}

	public CopycatModMain() {
		BaseMod.subscribe(this);

		BaseMod.addColor(CharacterEnum.CardColorEnum.COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE,
				COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE, makePath(ATTACK_COPYCAT_BLUE),
				makePath(SKILL_COPYCAT_BLUE), makePath(POWER_COPYCAT_BLUE),
				makePath(ENERGY_ORB_COPYCAT_BLUE), makePath(ATTACK_COPYCAT_BLUE_PORTRAIT),
				makePath(SKILL_COPYCAT_BLUE_PORTRAIT), makePath(POWER_COPYCAT_BLUE_PORTRAIT),
				makePath(ENERGY_ORB_COPYCAT_BLUE_PORTRAIT), makePath(CARD_ENERGY_ORB));

		BaseMod.addColor(CharacterEnum.CardColorEnum.COPYCAT_MONSTER, COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE,
				COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE, COPYCAT_BLUE, makePath(ATTACK_COPYCAT_BLUE),
				makePath(SKILL_COPYCAT_BLUE), makePath(POWER_COPYCAT_BLUE),
				makePath(ENERGY_ORB_COPYCAT_BLUE), makePath(ATTACK_COPYCAT_BLUE_PORTRAIT),
				makePath(SKILL_COPYCAT_BLUE_PORTRAIT), makePath(POWER_COPYCAT_BLUE_PORTRAIT),
				makePath(ENERGY_ORB_COPYCAT_BLUE_PORTRAIT), makePath(CARD_ENERGY_ORB));
	}

	@SuppressWarnings("unused")
	public static void initialize() {
		CopycatModMain mod = new CopycatModMain();
		isDragonTamerLoaded = Loader.isModLoaded("DTMod");
	}

	@Override
	public void receiveEditCharacters() {
		BaseMod.addCharacter(new Copycat(Copycat.charStrings.NAMES[1], CharacterEnum.PlayerClassEnum.THE_COPYCAT),
				makePath(THE_COPYCAT_BUTTON), makePath(THE_COPYCAT_PORTRAIT), CharacterEnum.PlayerClassEnum.THE_COPYCAT);

		receiveEditPotions();
	}

	void receiveEditPotions() {

	}

	@Override
	public void receivePostInitialize() {
		Texture badgeTexture = new Texture(makePath(BADGE_IMAGE));

		ModPanel settingsPanel = new ModPanel();

		BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
		DynamicCard.initializeDescriptionParts();
		AbstractMonsterCard.autoAddLibrary();

		ConsoleCommand.addCommand("redirect", RedirectCommand.class);
	}

	@Override
	public void receiveEditCards() {
		new AutoAdd(MOD_ID)
				.packageFilter(Mimic.class)
				.notPackageFilter(AbstractMonsterCard.class)
				.notPackageFilter(ChooseProtective.class)
				.setDefaultSeen(false)
				.cards();
		new AutoAdd(MOD_ID)
				.packageFilter(AbstractMonsterCard.class)
				.setDefaultSeen(false)
				.cards();
	}

	@Override
	public void receiveEditRelics() {
		BaseMod.addRelicToCustomPool(new LuckyBag(), CharacterEnum.CardColorEnum.COPYCAT_BLUE);
	}

	@Override
	public void receiveEditStrings() {
		loadLocStrings("eng");
		if (Settings.language != Settings.GameLanguage.ENG) {
			loadLocStrings(Settings.language.toString().toLowerCase());
		}
	}

	private void loadLocStrings(String loc) {
		try {
			if (Gdx.files.internal(MOD_ID + "/localization/" + loc).exists()) {
				BaseMod.loadCustomStrings(BlightStrings.class, GetLocString(loc, "blights"));
				BaseMod.loadCustomStrings(CardStrings.class, GetLocString(loc, "cards"));
				BaseMod.loadCustomStrings(CharacterStrings.class, GetLocString(loc, "characters"));
				BaseMod.loadCustomStrings(EventStrings.class, GetLocString(loc, "events"));
				BaseMod.loadCustomStrings(MonsterStrings.class, GetLocString(loc, "monsters"));
				BaseMod.loadCustomStrings(PotionStrings.class, GetLocString(loc, "potions"));
				BaseMod.loadCustomStrings(PowerStrings.class, GetLocString(loc, "powers"));
				BaseMod.loadCustomStrings(RelicStrings.class, GetLocString(loc, "relics"));
				BaseMod.loadCustomStrings(RunModStrings.class, GetLocString(loc, "run_mods"));
				BaseMod.loadCustomStrings(StanceStrings.class, GetLocString(loc, "stances"));
				BaseMod.loadCustomStrings(TutorialStrings.class, GetLocString(loc, "tutorials"));
				BaseMod.loadCustomStrings(UIStrings.class, GetLocString(loc, "ui"));
			}
		} catch (Exception ignore) {

		}
	}

	@Override
	public void receiveEditKeywords() {
		loadLocKeywords("eng");
		if (Settings.language != Settings.GameLanguage.ENG) {
			loadLocKeywords(Settings.language.toString().toLowerCase());
		}
	}

	private void loadLocKeywords(String loc) {
		if (Gdx.files.internal(MOD_ID + "/localization/" + loc).exists()) {
			String json = GetLocString(loc, "keywords");
			com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = BaseMod.gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);
			for (Keyword keyword : keywords) {
				BaseMod.addKeyword(MOD_ID.toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
			}
		}
	}

	public static String makeID(String idText) {
		return MOD_ID + ":" + idText;
	}

	public static String makeLowerID(String idText) {
		return (MOD_ID + ":" + idText).toLowerCase();
	}

	public static String GetCardPath(String id) {
		return MOD_ID + "/images/cards/" + id + ".png";
	}

	public static String GetPowerPath(String id, int size) {
		return MOD_ID + "/images/powers/" + id + "_" + size + ".png";
	}

	public static String GetMinionPath(String id) {
		return MOD_ID + "/images/char/Minions/" + id + ".png";
	}

	public static String GetRelicPath(String id) {
		return MOD_ID + "/images/relics/" + id + ".png";
	}

	public static String GetBlightPath(String id) {
		return MOD_ID + "/images/blights/" + id + ".png";
	}

	public static String GetBlightOutlinePath(String id) {
		return MOD_ID + "/images/blights/outline/" + id + ".png";
	}

	public static String GetRelicOutlinePath(String id) {
		return MOD_ID + "/images/relics/outline/" + id + ".png";
	}

	public static String GetEventPath(String id) {
		return MOD_ID + "/images/events/" + id + ".png";
	}

	private static String GetLocString(String locCode, String name) {
		return Gdx.files.internal(MOD_ID + "/localization/" + locCode + "/" + name + ".json").readString(
				String.valueOf(StandardCharsets.UTF_8));
	}

	public static AbstractMonsterCard getEnemyLastMoveCard(AbstractMonster m) {
		AbstractMonsterCard c = CaptureEnemyMovePatch.lastMoveCards.get(m);
		if (c == null) {
			return NoMove.preview;
		} else {
			return c;
		}
	}

	public static void clearMonsterCards() {
		CaptureEnemyMovePatch.generatedCards.clear();
		CaptureEnemyMovePatch.lastMoveCards.clear();
	}

	@Override
	public void receivePreStartGame() {
		clearMonsterCards();
		SubstituteMinion.instance = new SubstituteMinion();
	}

	@Override
	public void receiveOnBattleStart(AbstractRoom room) {
		Charging.useCount = 0;
		BetterFriendlyMinionsUtils.copycatMinions = new AbstractCopycatMinion[4];
		BetterFriendlyMinionsUtils.minionAiRng = new Random(Settings.seed + AbstractDungeon.floorNum);
		BetterFriendlyMinionsUtils.enemyTargetRng = new Random(Settings.seed + AbstractDungeon.floorNum);
		Spiredex.monsterCardRng = new Random(Settings.seed + AbstractDungeon.floorNum);
		TextEffect.messageIndexSet.clear();
		TextEffect.positionSet.clear();
		VenomologyPower.disabled = false;
		VenomologyActivateAction.poisonPowers.clear();
		TargetAllyPatch.cardTargetMap.clear();
		SubstituteMinion.instance.clearPowers();
	}

	@Override
	public void receiveCustomModeMods(List<CustomMod> list) {
		list.add(new CustomMod(makeID("MonsterOnly"), "y", true));
	}

	@Override
	public void receivePostCreateStartingDeck(AbstractPlayer.PlayerClass chosenClass, CardGroup addCardsToMe) {
		if (chosenClass == CharacterEnum.PlayerClassEnum.THE_COPYCAT) {
			AbstractCard cardToUpgrade = null;
			for (AbstractCard c : addCardsToMe.group) {
				if (c.cardID.equals(Mimic.ID)) {
					cardToUpgrade = c;
				}
			}
			if (cardToUpgrade != null) {
				cardToUpgrade.upgrade();
			}
		}
	}

	public static boolean strengthThreshold() {
		if (AbstractDungeon.player == null) return false;
		AbstractPower pow = AbstractDungeon.player.getPower(StrengthPower.POWER_ID);
		return pow != null && pow.amount >= 6;
	}
}
