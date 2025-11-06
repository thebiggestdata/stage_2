package com.thebiggestdata;

import com.thebiggestdata.infrastructure.adapter.in.cli.CliAdapter;
import com.thebiggestdata.infrastructure.adapter.in.rest.RestApiAdapter;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "server".equalsIgnoreCase(args[0])) {
            System.out.println("Starting REST API Server...");
            RestApiAdapter restApiAdapter = new RestApiAdapter();
            String[] serverArgs = new String[args.length - 1];
            System.arraycopy(args, 1, serverArgs, 0, args.length - 1);
            restApiAdapter.run(serverArgs);
        } else {
            System.out.println("Starting CLI mode...");
            CliAdapter cliAdapter = new CliAdapter();
            cliAdapter.run(args);
        }
    }
}
