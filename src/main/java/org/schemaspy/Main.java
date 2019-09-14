/*
 * Copyright (C) 2004 - 2011 John Currier
 * Copyright (C) 2016 Rafal Kasa
 * Copyright (C) 2017 Wojciech Kasa
 * Copyright (C) 2017 Thomas Traude
 * Copyright (C) 2017 Ismail Simsek
 * Copyright (C) 2017 Daniel Watt
 * Copyright (C) 2018 Nils Petzaell
 *
 * This file is a part of the SchemaSpy project (http://schemaspy.org).
 *
 * SchemaSpy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.schemaspy;

import lombok.extern.slf4j.Slf4j;
import org.schemaspy.cli.SchemaSpyRunner;
import org.schemaspy.logging.StackTraceOmitter;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John Currier
 * @author Rafal Kasa
 * @author Wojciech Kasa
 * @author Thomas Traude
 * @author Ismail Simsek
 * @author Daniel Watt
 * @author Nils Petzaell
 */
@Slf4j
@SpringBootApplication
@EnableAsync
public class Main {
    public static void main(String... args) {
        if (List.of(args).indexOf("-servermode") == -1) {
            ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
            SchemaSpyRunner schemaSpyRunner = context.getBean(SchemaSpyRunner.class);
            schemaSpyRunner.run(args);
            if (StackTraceOmitter.hasOmittedStackTrace()) {
                log.info("StackTraces have been omitted, use `-debug` when executing SchemaSpy to see them");
            }
            int exitCode = SpringApplication.exit(context, () -> 0);
            System.exit(exitCode);
        } else {
            log.info("Web service mode");
            SpringApplication.run(Main.class, args);
        }
    }
}