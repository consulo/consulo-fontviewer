/**
 * @author VISTALL
 * @since 19-Sep-22
 */
module consulo.fontviewer.api
{
	requires transitive consulo.ide.api;

	exports consulo.fontviewer;
	exports consulo.fontviewer.icon;
	exports consulo.fontviewer.internal to consulo.fontviewer.desktop.awt, consulo.fontviewer;
}