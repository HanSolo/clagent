package eu.hansolo.clagent;

import com.sun.tools.attach.*;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;


public class Main {

    public static void main(String[] args) throws IOException, AgentLoadException, AgentInitializationException, AttachNotSupportedException {
        //Get all running virtual machines in the current system
        System.out.println("CLAgent start...");
        if (null == args || args.length == 0) {
            System.out.println("Use it as follows: java -jar clagent-17.0.0.jar attach YOUR.PACKAGE.MAIN /PATH/TO/YOUR/JAR/YOUR_AWESOME.JAR");
            System.exit(0);
        }
        String                         option = args[0];
        List<VirtualMachineDescriptor> list   = VirtualMachine.list();
        if (option.equals("list")) {
            for (VirtualMachineDescriptor vmd : list) {
                System.out.println(vmd.displayName());
            }
        } else if (option.equals("attach")) {
            String jProcessName = args[1];
            String agentPath = args[2];
            for (VirtualMachineDescriptor vmd : list) {
                if (vmd.displayName().equals(jProcessName)) {
                    VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                    //Then load agent.jar and send it to the virtual machine
                    virtualMachine.loadAgent(agentPath);
                }
            }
        }
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("CLAgent -> agentMain -> agentArgs: " + agentArgs);
        Class<?>[] classes = instrumentation.getAllLoadedClasses();
        for (Class<?> cls : classes) {
            System.out.println("CLAgent get loaded class: " + cls.getName());
        }
        instrumentation.addTransformer(new DefineTransformer(), true);
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("CLAgent -> premain -> agentArgs : " + agentArgs);
        Class<?>[] cLasses = instrumentation.getAllLoadedClasses();
        for (Class<?> cls : cLasses) {
            System.out.println("CLAgent get loaded class:" + cls.getName());
        }
        instrumentation.addTransformer(new DefineTransformer(), true);
    }

    static class DefineTransformer implements ClassFileTransformer {
        @Override public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            System.out.println("CLAgent transform Class:" + className);
            return classfileBuffer;
        }
    }
}