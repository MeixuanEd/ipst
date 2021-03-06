/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * Copyright (c) 2016-2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.online.tools;

import com.google.auto.service.AutoService;
import com.powsybl.tools.Command;
import com.powsybl.tools.Tool;
import com.powsybl.tools.ToolRunningContext;
import eu.itesla_project.modules.online.OnlineConfig;
import eu.itesla_project.modules.online.OnlineDb;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Quinary <itesla@quinary.com>
 */
@AutoService(Tool.class)
public class ListOnlineWorkflowStatesTool implements Tool {

    private static Command COMMAND = new Command() {

        @Override
        public String getName() {
            return "list-online-workflow-states";
        }

        @Override
        public String getTheme() {
            return Themes.ONLINE_WORKFLOW;
        }

        @Override
        public String getDescription() {
            return "List stored states ids of an online workflow";
        }

        @Override
        public Options getOptions() {
            Options options = new Options();
            options.addOption(Option.builder().longOpt("workflow")
                    .desc("the workflow id")
                    .hasArg()
                    .required()
                    .argName("ID")
                    .build());
            return options;
        }

        @Override
        public String getUsageFooter() {
            return null;
        }

    };

    @Override
    public Command getCommand() {
        return COMMAND;
    }

    @Override
    public void run(CommandLine line, ToolRunningContext context) throws Exception {
        OnlineConfig config = OnlineConfig.load();
        OnlineDb onlinedb = config.getOnlineDbFactoryClass().newInstance().create();
        String workflowId = line.getOptionValue("workflow");
        List<Integer> storedStates = onlinedb.listStoredStates(workflowId);
        if (!storedStates.isEmpty()) {
            context.getOutputStream().println("Stored States = " + storedStates.toString());
        } else {
            context.getOutputStream().println("No stored states for this workflow");
        }

        Map<Integer, Set<String>> storedPostContingenciesStates = onlinedb.listStoredPostContingencyStates(workflowId);
        if (!storedPostContingenciesStates.isEmpty()) {
            context.getOutputStream().println("Stored post-contingencies states ( state id, contingencies ids ) = " + storedPostContingenciesStates.toString());
        } else {
            context.getOutputStream().println("No stored post-contingencies states for this workflow");
        }
        onlinedb.close();
    }

}
