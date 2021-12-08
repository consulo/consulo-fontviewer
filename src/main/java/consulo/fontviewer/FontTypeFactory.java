package consulo.fontviewer;

import com.android.tools.idea.fileTypes.FontFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;

import javax.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 08/12/2021
 */
public class FontTypeFactory extends FileTypeFactory
{
	@Override
	public void createFileTypes(@Nonnull FileTypeConsumer fileTypeConsumer)
	{
		fileTypeConsumer.consume(FontFileType.INSTANCE, "otf;ttf");
	}
}
