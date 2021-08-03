package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SuperDelayAction;
import TheCopycat.actions.VenomologyActivateAction;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;

public class VenomologyPower extends AbstractPower {
	public static final String RAW_ID = "VenomologyPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
	public static final TextureAtlas.AtlasRegion IMG128 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 128)), 0, 0, 84, 84);
	public static final TextureAtlas.AtlasRegion IMG48 = new TextureAtlas.AtlasRegion(
			ImageMaster.loadImage(CopycatModMain.GetPowerPath(RAW_ID, 48)), 0, 0, 32, 32);

	public static boolean disabled = false;

	public VenomologyPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.updateDescription();
		this.type = AbstractPower.PowerType.BUFF;
		this.region128 = IMG128;
		this.region48 = IMG48;
	}

	@Override
	public void updateDescription() {
		description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
	}

	@Override
	public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
		if (!disabled && power.type == PowerType.DEBUFF && !power.ID.equals(GainStrengthPower.POWER_ID) && source == owner && target != owner && !target.hasPower(ArtifactPower.POWER_ID)) {
			flash();
			VenomologyActivateAction action = new VenomologyActivateAction(target, source, amount);
			VenomologyActivateAction.lastAction = action;
			addToBot(new SuperDelayAction(() -> {
				if (VenomologyActivateAction.lastAction == action) {
					addToBot(action);
				}
			}));

			addToBot(new AbstractGameAction() {
				@Override
				public void update() {

					isDone = true;
				}
			});
		}
	}
}
