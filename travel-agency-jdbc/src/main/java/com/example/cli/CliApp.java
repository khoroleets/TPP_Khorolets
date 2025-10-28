package com.example.cli;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CliApp {
    public static void main(String[] args) throws Exception {
        CommandExecutor exec = new CommandExecutor();

        if (args.length > 0) {
            // виконати одну команду з аргументу
            String cmd = String.join(" ", args);
            System.out.println(exec.execute(cmd));
            return;
        }

        System.out.println("Enter commands (insert/update/delete/read). Type 'exit' to quit.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while (true) {
            System.out.print("> ");
            line = br.readLine();
            if (line == null) break;
            line = line.trim();
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;
            if (line.isEmpty()) continue;
            String out = exec.execute(line);
            System.out.println(out);
        }
        System.out.println("Bye.");
    }
}
