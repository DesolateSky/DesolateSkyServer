package net.desolatesky.item.tool.part;

import net.desolatesky.item.tool.part.type.AxeHead;
import net.desolatesky.item.tool.part.type.NoActionToolPart;
import net.desolatesky.util.Namespace;

public final class ToolParts {

    public static final ToolPart AXE_HEAD = new AxeHead(Namespace.key("axe_head"));
    public static final ToolPart BINDING = new NoActionToolPart(Namespace.key("binding"), ToolPartType.BINDING);
    public static final ToolPart HANDLE = new NoActionToolPart(Namespace.key("handle"), ToolPartType.HANDLE);

}
