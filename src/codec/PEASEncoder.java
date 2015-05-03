package codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

import protocol.PEASObject;

public class PEASEncoder extends MessageToByteEncoder<PEASObject>{
	
	 // TODO Use CharsetEncoder instead.
    private final Charset charset;

    /**
     * Creates a new instance with the current system character set.
     */
    public PEASEncoder() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates a new instance with the specified character set.
     */
    public PEASEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    /*
	@Override
	protected void encode(ChannelHandlerContext ctx, PEASObject obj, List<Object> out) throws Exception {
		out.write(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(obj.getHeader().toString()), charset));
		out.add(obj.getBody());
		//out.add(obj.toString());
	}
     */
    
	@Override
	protected void encode(ChannelHandlerContext ctx, PEASObject obj, ByteBuf out) throws Exception {
		out.writeBytes(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(obj.getHeader().toString()), charset));
		out.writeBytes(obj.getBody().getBody());
	}
}
