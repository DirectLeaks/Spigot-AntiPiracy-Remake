package me.itzsomebody.spigotantipiracy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class InjectAntiPiracy {
	private static final List<String> placeholders = new ArrayList<String>() {
		{
			this.add("%%__USER__%%");
		    this.add("%%__NONCE__%%");
		    this.add("%%__RESOURCE__%%");
		}
	};	
	private File file;
    private String antiPiracyLink;
    private IDs ids;
    
    public InjectAntiPiracy(File file, String userID, String resourceID, String antiPiracyLink) {    	
        this.file = file;
        this.ids = new IDs(userID, resourceID);
        this.antiPiracyLink = antiPiracyLink;
    }
    
    public InputStream getInjectedFile() throws Throwable {
        InputStream input;
        ZipFile jar = new ZipFile(this.file);
        Enumeration<? extends ZipEntry> jarEntries = jar.entries();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZipOutputStream jarOutput = new ZipOutputStream(output);
        while (jarEntries.hasMoreElements()) {
            boolean modified = false;
            ZipEntry entry = jarEntries.nextElement();
            ClassNode classNode = new ClassNode();
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                ClassReader classReader = new ClassReader(jar.getInputStream(entry));
                classReader.accept(classNode, 0);
                if (classNode.superName.equals("org/bukkit/plugin/java/JavaPlugin") || classNode.superName.equals("net/md_5/bungee/api/plugin/Plugin")) {
                    modified = true;
                    if (classNode.version >= 50) { // Java 6
                        for (MethodNode method : classNode.methods) {
                            ListIterator<AbstractInsnNode> insns = method.instructions.iterator();
                            while (insns.hasNext()) {
								Object cst;
                                AbstractInsnNode insn = insns.next();
                                if (!(insn instanceof LdcInsnNode) || !((cst = ((LdcInsnNode)insn).cst) instanceof String) || !InjectAntiPiracy.containsPlaceHolders((String)((LdcInsnNode)insn).cst)) continue;
                                ((LdcInsnNode)insn).cst = InjectAntiPiracy.replacePlaceholders((String)((LdcInsnNode)insn).cst, this.ids);
                                modified = true;
                            }
                        }
                    }
                    MethodNode antipiracy = null;
                    for (MethodNode method : classNode.methods) {
                        if ((method.name.equals("onEnable") && method.desc.equals("()V"))) {
                        	method.instructions.insert(new MethodInsnNode(184, classNode.name, "loadConfig0", "()V", false));
                        	antipiracy = InjectAntiPiracy.loadConfig0(this.antiPiracyLink, this.ids.getUserID(), this.ids.getResourceID(), this.ids);
                        	break;
                        }
                    }
                    if (antipiracy != null) {
                        classNode.methods.add(antipiracy);
                    }
                } 
                for (MethodNode method : classNode.methods) {
                    ListIterator<AbstractInsnNode> insns = method.instructions.iterator();
                    while (insns.hasNext()) {
                        Object cst;
                        AbstractInsnNode insn = insns.next();
                        if (!(insn instanceof LdcInsnNode) || !((cst = ((LdcInsnNode)insn).cst) instanceof String) || !InjectAntiPiracy.containsPlaceHolders((String)((LdcInsnNode)insn).cst)) continue;
                        ((LdcInsnNode)insn).cst = InjectAntiPiracy.replacePlaceholders((String)((LdcInsnNode)insn).cst, this.ids);
                        modified = true;
                    }
                }
            }
            if (!modified) {
                input = jar.getInputStream(entry);
            } else {
                ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);
                input = new ByteArrayInputStream(classWriter.toByteArray());
            }
            ZipEntry newEntry = new ZipEntry(entry.getName());
            jarOutput.putNextEntry(newEntry);
            InjectAntiPiracy.writeToOut(jarOutput, input);
        }
        jarOutput.close();
        jar.close();
        jar = null;
        jarOutput = null;
        input = null;
        System.gc();
        return new ByteArrayInputStream(output.toByteArray());
    }

    private static MethodNode loadConfig0(String link, String userID, String resourceID, IDs ids) {
    	MethodNode method = new MethodNode(4170, "loadConfig0", "()V", null, null);
        method.visitMaxs(5, 2);
        LabelNode start = new LabelNode(new Label());
        LabelNode constart = new LabelNode(new Label());
        LabelNode responcestart = new LabelNode(new Label());
        LabelNode end = new LabelNode(new Label());
        LabelNode handler = new LabelNode(new Label());
        LabelNode returnLabel = new LabelNode(new Label());
        LocalVariableNode con = new LocalVariableNode("con", "Ljava/net/URLConnection;", null, constart, end, 0);
        LocalVariableNode responce = new LocalVariableNode("response", "Ljava/lang/String;", null, responcestart, end, 0);
        method.localVariables.add(con);
        method.localVariables.add(responce);
        method.tryCatchBlocks.add(new TryCatchBlockNode(start, end, handler, "java/io/IOException"));
        InsnList insns = new InsnList();
        insns.add((AbstractInsnNode)new TypeInsnNode(187, "java/net/URL")); // new java/net/URL
        insns.add((AbstractInsnNode)new InsnNode(89)); // dup
        insns.add((AbstractInsnNode)new LdcInsnNode(((String)link.replace((CharSequence)"%%__USER__%%", (CharSequence)userID).replace((CharSequence)"%%__NONCE__%%", (CharSequence)ids.generateNonceID()).replace((CharSequence)"%%__RESOURCE__%%", (CharSequence)resourceID))));
        insns.add((AbstractInsnNode)new MethodInsnNode(183, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false)); // invokespecial java/net/URL <init> (Ljava/lang/String;)V
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/net/URL", "openConnection", "()Ljava/net/URLConnection;", false)); // invokevirtual java/net/URL openConnection ()Ljava/net/URLConnection;
        insns.add((AbstractInsnNode)new VarInsnNode(58, 0)); // astore_0
        insns.add((AbstractInsnNode)constart);
        insns.add((AbstractInsnNode)new VarInsnNode(25, 0)); // aload_0
        insns.add((AbstractInsnNode)new IntInsnNode(17, 1000)); // sipush 1000
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/net/URLConnection", "setConnectTimeout", "(I)V", false)); // invokevirtual Method java/net/URLConnection setConnectTimeout (I)V 
        insns.add((AbstractInsnNode)new VarInsnNode(25, 0)); // aload_0
        insns.add((AbstractInsnNode)new IntInsnNode(17, 1000)); // sipush 1000
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/net/URLConnection", "setReadTimeout", "(I)V", false)); // invokevirtual java/net/URLConnection setReadTimeout (I)V
        insns.add((AbstractInsnNode)new VarInsnNode(25, 0)); // aload_0
        insns.add((AbstractInsnNode)new TypeInsnNode(192, "java/net/HttpURLConnection")); // checkcast java/net/HttpURLConnection
        insns.add((AbstractInsnNode)new InsnNode(4)); // iconst_1
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/net/HttpURLConnection", "setInstanceFollowRedirects", "(Z)V", false)); // invokevirtual java/net/HttpURLConnection setInstanceFollowRedirects (Z)V
        insns.add((AbstractInsnNode)new TypeInsnNode(187, "java/io/BufferedReader")); // new java/io/BufferedReader
        insns.add((AbstractInsnNode)new InsnNode(89)); // dup
        insns.add((AbstractInsnNode)new TypeInsnNode(187, "java/io/InputStreamReader")); // new java/io/InputStreamReader
        insns.add((AbstractInsnNode)new InsnNode(89)); // dup
        insns.add((AbstractInsnNode)new VarInsnNode(25, 0)); // aload_0
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/net/URLConnection", "getInputStream", "()Ljava/io/InputStream;", false)); // invokevirtual java/net/URLConnection getInputStream ()Ljava/io/InputStream;
        insns.add((AbstractInsnNode)new MethodInsnNode(183, "java/io/InputStreamReader", "<init>", "(Ljava/io/InputStream;)V", false)); // invokespecial java/io/InputStreamReader <init> (Ljava/io/InputStream;)V
        insns.add((AbstractInsnNode)new MethodInsnNode(183, "java/io/BufferedReader", "<init>", "(Ljava/io/Reader;)V", false)); // invokespecial java/io/BufferedReader <init> (Ljava/io/Reader;)V
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/io/BufferedReader", "readLine", "()Ljava/lang/String;", false)); // invokevirtual java/io/BufferedReader readLine ()Ljava/lang/String;
        insns.add((AbstractInsnNode)new VarInsnNode(58, 1)); // astore_1
        insns.add((AbstractInsnNode)responcestart);
        insns.add((AbstractInsnNode)new LdcInsnNode((String)"false")); // ldc false
        insns.add((AbstractInsnNode)new VarInsnNode(25, 1)); // aload_1
        insns.add((AbstractInsnNode)new MethodInsnNode(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false)); // invokevirtual java/lang/String equals (Ljava/lang/Object;)Z
        insns.add((AbstractInsnNode)new JumpInsnNode(153, end)); // ifeq 6
        insns.add((AbstractInsnNode)new TypeInsnNode(187, "java/lang/RuntimeException")); // new java/lang/RuntimeException
        insns.add((AbstractInsnNode)new InsnNode(89)); // dup
        insns.add((AbstractInsnNode)new LdcInsnNode((Object)"Access to this plugin has been disabled! Please contact the author!")); // ldc Access to this plugin has been disabled! Please contact the author!
        insns.add((AbstractInsnNode)new MethodInsnNode(183, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false)); // invokespecial Method java/lang/RuntimeException <init> (Ljava/lang/String;)V
        insns.add((AbstractInsnNode)new InsnNode(191)); // athrow
        insns.add((AbstractInsnNode)end);
        insns.add((AbstractInsnNode)new FrameNode(3, 0, null, 0, null)); // frame same
        insns.add((AbstractInsnNode)new JumpInsnNode(167, returnLabel)); // goto 4
        insns.add((AbstractInsnNode)handler);
        insns.add((AbstractInsnNode)new FrameNode(4, 0, null, 1, new Object[]{"java/io/IOException"})); // frame same1 stack [java/io/IOException]
        insns.add((AbstractInsnNode)new VarInsnNode(58, 0)); // astore_0
        insns.add((AbstractInsnNode)returnLabel);
        insns.add((AbstractInsnNode)new FrameNode(3, 0, null, 0, null)); // frame same
        insns.add((AbstractInsnNode)new InsnNode(177)); // return
        method.instructions = insns;
        return method;
    }
    
    private static void writeToOut(ZipOutputStream outputStream, InputStream inputStream) throws Throwable {
        byte[] buffer = new byte[4096];
        try {
            while (inputStream.available() > 0) {
                int data = inputStream.read(buffer);
                outputStream.write(buffer, 0, data);
            }
        } finally {
            inputStream.close();
            outputStream.closeEntry();
        }
    }
    
    private static boolean containsPlaceHolders(String string) {
        for (final String s : placeholders) {
            if (string.contains(s)) {
            	return true;
            }
        }
		return false;
    }

    private static String replacePlaceholders(String original, IDs ids) {
        return original.replace("%%__USER__%%", ids.getUserID()).replace("%%__NONCE__%%", ids.generateNonceID()).replace("%%__RESOURCE__%%", ids.getResourceID());
    }
}
