package TheCopycat.commands;

import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import basemod.DevConsole;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.ConvertHelper;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class RedirectCommand extends ConsoleCommand {

	public RedirectCommand() {
		minExtraTokens = 1;
		maxExtraTokens = 2;
		simpleCheck = true;
		requiresPlayer = true;
	}

	@Override
	public void execute(String[] tokens, int depth) {
		if (tokens.length < 2) {
			help();
		} else {
			Integer i = ConvertHelper.tryParseInt(tokens[1]);
			if (i == null) {
				help();
				return;
			}
			ArrayList<Integer> indexes = getIndexes();
			if (indexes.contains(i)) {
				AbstractFriendlyMonster minion = null;
				if (i == 4) {
					minion = SubstituteMinion.instance;
				} else if (i >= 0 && i <= 3) {
					minion = BetterFriendlyMinionsUtils.copycatMinions[i];
				}

				if (tokens.length > 2 && tokens[2].equalsIgnoreCase("all")) {
					for (AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
						if (!m.isDying) {
							BetterFriendlyMinionsUtils.switchTarget(m, minion, false);
						}
					}
				} else {
					new ConsoleTargetedRedirect(minion);
				}
			} else {
				DevConsole.log("could not find ally with index #" + i);
			}
		}
	}

	ArrayList<Integer> getIndexes() {
		ArrayList<Integer> indexes = new ArrayList<>();
		ArrayList<AbstractCreature> list = BetterFriendlyMinionsUtils.getAllyList();
		for (AbstractCreature c : list) {
			if (c instanceof AbstractCopycatMinion) {
				indexes.add(((AbstractCopycatMinion) c).index);
			} else if (c == AbstractDungeon.player) {
				indexes.add(-1);
			}
		}
		Collections.sort(indexes);
		return indexes;
	}

	@Override
	public ArrayList<String> extraOptions(String[] tokens, int depth) {
		if (tokens.length == 2) {
			return getIndexes().stream().map(Object::toString).collect(Collectors.toCollection(ArrayList::new));
		} else if (tokens.length == 3) {
			return new ArrayList<String>() {
				{
					add("all");
				}
			};
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public void errorMsg() {
		help();
	}

	private static void help() {
		DevConsole.couldNotParse();
		DevConsole.log("options are:");
		DevConsole.log("* [ally index]");
		DevConsole.log("* [ally index] all");
		DevConsole.log("  * ally index: -1=Player, 0-3=normal minions, 4=Substitute");
	}
}
