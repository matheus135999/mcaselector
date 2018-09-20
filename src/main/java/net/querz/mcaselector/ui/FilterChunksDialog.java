package net.querz.mcaselector.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.querz.mcaselector.filter.Comparator;
import net.querz.mcaselector.filter.GroupFilter;
import net.querz.mcaselector.filter.Operator;
import net.querz.mcaselector.filter.XPosFilter;
import net.querz.mcaselector.filter.ZPosFilter;
import net.querz.mcaselector.util.Debug;

public class FilterChunksDialog extends Dialog<FilterChunksDialog.Result> {

	private static GroupFilter gf = new GroupFilter();
	static {
		gf.addFilter(new XPosFilter(Operator.AND, Comparator.SEQ, 100));
		gf.addFilter(new XPosFilter(Operator.AND, Comparator.LEQ, -100));
		gf.addFilter(new ZPosFilter(Operator.AND, Comparator.SEQ, 100));
		gf.addFilter(new ZPosFilter(Operator.AND, Comparator.LEQ, -100));
	}

	private GroupFilter value = gf;
	private GroupFilterBox groupFilterBox = new GroupFilterBox(null, value, true);
	private ToggleGroup toggleGroup = new ToggleGroup();
	private RadioButton delete = new RadioButton("Delete");
	private RadioButton export = new RadioButton("Export");
	private RadioButton select = new RadioButton("Select");

	public FilterChunksDialog(Stage primaryStage) {
		setTitle("Filter chunks");

		initStyle(StageStyle.UTILITY);

		getDialogPane().getStyleClass().add("filter-dialog-pane");

		setResultConverter(p -> p == ButtonType.OK ? new Result(value, getHandleType()) : null);

		//apply same stylesheets to this dialog
		getDialogPane().getStylesheets().addAll(primaryStage.getScene().getStylesheets());

		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		toggleGroup.getToggles().addAll(delete, export, select);
		export.fire();

		setResizable(true);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(groupFilterBox);
		groupFilterBox.prefWidthProperty().bind(scrollPane.prefWidthProperty());

		groupFilterBox.setOnUpdate(f -> {
			getDialogPane().lookupButton(ButtonType.OK).setDisable(!value.isValid());
			Debug.dump(value);
		});

		VBox box = new VBox();
		box.getChildren().addAll(scrollPane,  new Separator(), delete, export, select);
		getDialogPane().setContent(box);
	}

	private HandleType getHandleType() {
		if (delete.isSelected()) {
			return HandleType.DELETE;
		} else if (export.isSelected()) {
			return HandleType.EXPORT;
		} else if (select.isSelected()) {
			return HandleType.SELECT;
		}
		return null;
	}

	public class Result {

		private HandleType type;
		private GroupFilter filter;

		public Result(GroupFilter filter, HandleType type) {
			this.filter = filter;
			this.type = type;
		}

		public HandleType getType() {
			return type;
		}

		public GroupFilter getFilter() {
			return filter;
		}
	}

	public enum HandleType {
		DELETE, EXPORT, SELECT
	}
}
