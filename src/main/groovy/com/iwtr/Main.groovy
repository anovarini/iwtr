/**
 * Copyright 2012 Alessandro Novarini
 *
 * This file is part of the iwtr project.
 *
 * Iwtr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.iwtr

import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.PosixParser
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.OptionBuilder
import org.apache.commons.cli.ParseException

def options = buildCommand()

CommandLineParser parser = new PosixParser()
CommandLine cmd = null

try {
    cmd = parser.parse(options, args)

    0 == args.length ? printHelp(options) : executeCommands(cmd)

} catch (ParseException exception) {
    printHelp options
}

void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter()
    formatter.printHelp 'iwtr', options, true
}

void executeCommands(CommandLine cmd) {
    cmd.argList.each { println it }
}

Options buildCommand() {
    Options options = new Options()
    options.addOption OptionBuilder.withArgName('dir').hasArg().withDescription('specify the data directory').create('data')
    options.addOption OptionBuilder.withArgName('dir').hasArg().withDescription('specify the root project directory').isRequired().create('root')
    options.addOption OptionBuilder.withArgName('name').hasArg().withDescription('specify the module name to release').isRequired().create('module')
    options.addOption OptionBuilder.withArgName('type').hasArg().withDescription('specify the dependency manager').create('manager')
}
