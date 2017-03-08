package io.prplz.fastmod;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.util.List;

public class FastMod implements ITweaker, IClassTransformer {
    @Override
    public void acceptOptions(List<String> args, File gameDir, final File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(this.getClass().getName());
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (name.equals("bao") || name.equals("bee") || name.equals("net.minecraft.server.MinecraftServer")) {
            ClassNode classNode = new ClassNode();
            new ClassReader(bytes).accept(classNode, 0);
            for (MethodNode method : classNode.methods) {
                AbstractInsnNode insn = method.instructions.getFirst();
                while (insn != null) {
                    if (insn.getOpcode() == Opcodes.LDC) {
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if (ldc.cst.equals(20.0F) && name.equals("bao") && method.name.equals("<init>")) {
                            ldc.cst = 50F;
                        }
                        if (ldc.cst.equals(50L) && name.equals("net.minecraft.server.MinecraftServer") && method.name.equals("run")) {
                            ldc.cst = 20L;
                        }
                    }
                    if (insn.getOpcode() == Opcodes.ICONST_2 && name.equals("bee") && method.name.equals("a") && method.desc.equals("(Lbcb;)V")) {
                        method.instructions.set(insn, new LdcInsnNode(420)); // blaze it
                    }
                    insn = insn.getNext();
                }
            }
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
