package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.utils.GameLogicUtils;
import TheCopycat.utils.MonsterCardMoveInfo;
import basemod.AutoAdd;
import basemod.abstracts.CustomCard;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.ConfigUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@AutoAdd.Ignore
public abstract class AbstractMonsterCard extends CustomCard implements CustomSavable<String> {
	public static final String[] DESCRIPTORS = CardCrawlGame.languagePack.getUIString(CopycatModMain.makeID("Descriptors")).TEXT;
	private static final String RAW_ID = "MonsterCard";
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_MONSTER;
	public static HashMap<String, AbstractMonsterCard> specialMonsterCardLibrary = new HashMap<>();

	public String monsterModelID = null;

	public String monsterCardID;

	public AbstractMonsterCard(String id, String name, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target) {
		super(id, name, IMG, cost, rawDescription, type, COLOR, rarity, target);
		monsterCardID = id;
	}

	public AbstractMonsterCard(String id, String name, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target, String monsterID, int move) {
		this(id, name, cost, rawDescription, type, rarity, target);
		monsterCardID = id;
		loadTexture(monsterID + "_" + move);
	}

	public AbstractMonsterCard(String id, String name, int cost, String rawDescription, CardType type, CardRarity rarity, CardTarget target, String imagePath) {
		super(id, name, imagePath, cost, rawDescription, type, COLOR, rarity, target);
		monsterCardID = id;
	}

	public static AbstractMonsterCard createFromMetricID(String id) {
		int upgradeCount = 0;
		int index = id.lastIndexOf('+');
		if (index != -1) {
			try {
				upgradeCount = Integer.parseInt(id.substring(index + 1));
				id = id.substring(0, index);
			} catch (NumberFormatException ignore) {
			}
		}

		String monsterID = null;
		String[] tokens = id.split(GameLogicUtils.metricIdSeparator, -1);

		AbstractMonsterCard c = specialMonsterCardLibrary.get(tokens[0]);
		if (c == null) return null;

		String monsterCardID = id;
		if (tokens.length > 1) {
			monsterID = tokens[tokens.length - 1];
			monsterCardID = id.substring(0, id.length() - monsterID.length() - GameLogicUtils.metricIdSeparator.length());
		}

		c = (AbstractMonsterCard) c.makeCopy();
		c.loadFromTokens(tokens);
		c.monsterCardID = monsterCardID;
		for (int i = 0; i < upgradeCount; i++) {
			c.upgrade();
		}
		c.loadTexture(monsterID);
		return c;
	}

	public static void saveTexture(String id, Pixmap pixmap) {
		String filePath = ConfigUtils.CONFIG_DIR + File.separator + CopycatModMain.MOD_ID + File.separator + id;

		Pixmap pixmapHalf = new Pixmap(250, 190, pixmap.getFormat());
		pixmapHalf.drawPixmap(pixmap,
			0, 0, pixmap.getWidth(), pixmap.getHeight(),
			0, 0, pixmapHalf.getWidth(), pixmapHalf.getHeight()
		);
		PixmapIO.writePNG(new FileHandle(filePath + ".png"), pixmapHalf);
		PixmapIO.writePNG(new FileHandle(filePath + "_p.png"), pixmap);
		imgMap.remove(filePath + ".png");

		pixmapHalf.dispose();
		pixmap.dispose();
	}

	public static void autoAddLibrary() {
		AutoAdd auto = new AutoAdd(CopycatModMain.MOD_ID).packageFilter(DynamicCard.class);
		Collection<CtClass> foundClasses = auto.findClasses(AbstractMonsterCard.class);
		try {
			ClassPool pool = Loader.getClassPool();
			for (CtClass ctClass : foundClasses) {
				AbstractMonsterCard c = (AbstractMonsterCard) pool.getClassLoader().loadClass(ctClass.getName()).newInstance();
				if (c.monsterCardID != null) {
					specialMonsterCardLibrary.put(c.monsterCardID, c);
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> getCardDescriptors() {
		return Collections.singletonList(DESCRIPTORS[0]);
	}

	public void loadFromMonsterCardID(String id) {
		monsterCardID = id;
		loadFromTokens(id.split(GameLogicUtils.metricIdSeparator, -1));
	}

	public void loadFromTokens(String[] tokens) {
		// do nothing
	}

	public String getDynamicID() {
		return monsterCardID + GameLogicUtils.metricIdSeparator + monsterModelID;
	}

	@Override
	public String getMetricID() {
		String id = getDynamicID();

		if (upgraded) {
			id = id + "+";
			if (timesUpgraded > 0) {
				id = id + timesUpgraded;
			}
		}

		return id;
	}

	@Override
	public String onSave() {
		return getDynamicID();
	}

	@Override
	public void onLoad(String id) {
		int index = id.lastIndexOf(GameLogicUtils.metricIdSeparator);
		if (index != -1) {
			String monsterID = id.substring(index + GameLogicUtils.metricIdSeparator.length());
			loadTexture(monsterID);
			loadFromMonsterCardID(id.substring(0, index));
		}
	}

	@Override
	public Type savedType() {
		return String.class;
	}

	public boolean loadTexture(String id) {
		try {
			monsterModelID = id;
			textureImg = ConfigUtils.CONFIG_DIR + File.separator + CopycatModMain.MOD_ID + File.separator + id + ".png";
			loadCardImage(textureImg);
			return portrait != null;
		} catch (Exception ignore) {
			return false;
		}
	}

	public void monsterTurnApplyPowers(AbstractMonster owner, AbstractCreature target) {
		// damage
		float tmp = baseDamage;

		for (AbstractPower p : owner.powers) {
			tmp = p.atDamageGive(tmp, damageTypeForTurn, this);
		}

		for (AbstractPower p : target.powers) {
			p.atDamageReceive(tmp, damageTypeForTurn, this);
		}

		if (target instanceof AbstractPlayer) {
			tmp = ((AbstractPlayer) target).stance.atDamageReceive(tmp, damageTypeForTurn);
		}

		for (AbstractPower p : owner.powers) {
			p.atDamageFinalGive(tmp, damageTypeForTurn, this);
		}

		for (AbstractPower p : target.powers) {
			p.atDamageFinalReceive(tmp, damageTypeForTurn, this);
		}

		if (tmp < 0.0F) {
			tmp = 0.0F;
		}

		damage = MathUtils.floor(tmp);

		// block
		tmp = baseBlock;

		for (AbstractPower p : owner.powers) {
			tmp = p.modifyBlock(tmp, this);
		}

		for (AbstractPower p : owner.powers) {
			tmp = p.modifyBlockLast(tmp);
		}

		if (tmp < 0.0F) {
			tmp = 0.0F;
		}

		block = MathUtils.floor(tmp);
	}

	public abstract MonsterCardMoveInfo createMoveInfo(boolean isAlly);

	public abstract void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly);
}
