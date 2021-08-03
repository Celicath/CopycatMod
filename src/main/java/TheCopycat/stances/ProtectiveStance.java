package TheCopycat.stances;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.StanceStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.vfx.BorderFlashEffect;
import com.megacrit.cardcrawl.vfx.stance.StanceAuraEffect;
import com.megacrit.cardcrawl.vfx.stance.StanceChangeParticleGenerator;
import com.megacrit.cardcrawl.vfx.stance.WrathParticleEffect;
import com.megacrit.cardcrawl.vfx.stance.WrathStanceChangeParticle;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public class ProtectiveStance extends AbstractStance {
	private static final String RAW_ID = "Protective";
	public static final String STANCE_ID = CopycatModMain.makeID(RAW_ID);

	private static final StanceStrings stanceString = CardCrawlGame.languagePack.getStanceString(STANCE_ID);
	private static long sfxId = -1L;
	public static final int AMOUNT = 2;

	public ProtectiveStance() {
		ID = STANCE_ID;
		name = stanceString.NAME;
		updateDescription();
	}

	public void updateAnimation() {
		if (!Settings.DISABLE_EFFECTS) {
			particleTimer -= Gdx.graphics.getDeltaTime();
			if (particleTimer < 0.0F) {
				particleTimer = 0.05F;
				AbstractDungeon.effectsQueue.add(new WrathParticleEffect() {
					{
						color = new Color(MathUtils.random(0.25F, 0.5F), MathUtils.random(0.75F, 1.0F), 0.1F, 0.0F);
					}

					@Override
					public void update() {
						super.update();
						color.a *= 0.8f;
					}
				});
			}
		}

		particleTimer2 -= Gdx.graphics.getDeltaTime();
		if (particleTimer2 < 0.0F) {
			particleTimer2 = MathUtils.random(0.3F, 0.4F);
			AbstractDungeon.effectsQueue.add(new StanceAuraEffect(STANCE_ID) {
				{
					color = new Color(MathUtils.random(0.5F, 0.6F), 1.0F, MathUtils.random(0.3F, 0.4F), 0.0f);
				}

				@Override
				public void update() {
					super.update();
					color.a *= 0.75f;
				}
			});
		}
	}

	public void updateDescription() {
		description = stanceString.DESCRIPTION[0];
	}

	public void onEnterStance() {
		if (sfxId != -1L) {
			stopIdleSfx();
		}

		AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
			@Override
			public void update() {
				for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
					BetterFriendlyMinionsUtils.switchTarget(mo, null, false);
				}
				isDone = true;
			}
		});

		CardCrawlGame.sound.play("STANCE_ENTER_WRATH");
		sfxId = CardCrawlGame.sound.playAndLoop("STANCE_LOOP_WRATH");
		AbstractDungeon.effectsQueue.add(new BorderFlashEffect(Color.LIME, true));
		AbstractDungeon.effectsQueue.add(new StanceChangeParticleGenerator(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, STANCE_ID) {
			@Override
			public void update() {
				for (int i = 0; i < 20; ++i) {
					AbstractDungeon.effectsQueue.add(new WrathStanceChangeParticle(AbstractDungeon.player.hb.cX) {
						{
							color = new Color(MathUtils.random(0.4F, 0.6F), 1.0F, 0.1F, 0.0F);
						}
					});
				}
				isDone = true;
			}
		});
	}

	public void onExitStance() {
		stopIdleSfx();
		AbstractDungeon.actionManager.addToBottom(new AbstractGameAction() {
			@Override
			public void update() {
				ArrayList<AbstractMonster> minionList = BetterFriendlyMinionsUtils.getMinionList();
				if (!minionList.isEmpty()) {
					for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
						if (!mo.isDeadOrEscaped() && MonsterHelper.getTarget(mo) == null) {
							BetterFriendlyMinionsUtils.switchTarget(mo, (AbstractFriendlyMonster) minionList.get(BetterFriendlyMinionsUtils.enemyTargetRng.random(0, minionList.size() - 1)), false);
						}
					}
				}
				isDone = true;
			}
		});
	}

	public void stopIdleSfx() {
		if (sfxId != -1L) {
			CardCrawlGame.sound.stop("STANCE_LOOP_WRATH", sfxId);
			sfxId = -1L;
		}
	}
}
