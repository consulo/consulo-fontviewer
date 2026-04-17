/**
 * @author VISTALL
 * @since 19-Sep-22
 */
module consulo.fontviewer.api
{
	requires transitive consulo.file.editor.api;
	requires transitive consulo.project.api;
	requires transitive consulo.ui.api;
	requires transitive consulo.base.icon.library;
	requires transitive consulo.virtual.file.system.api;

	exports consulo.fontviewer;
	exports consulo.fontviewer.internal to consulo.fontviewer.desktop.awt, consulo.fontviewer;
}