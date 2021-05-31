package socialnetwork.controller;

import com.itextpdf.text.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.PopOver;
import socialnetwork.domain.User;
import socialnetwork.service.UserService;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RapoarteController {
    @FXML
    private ListView<String> listRapoarte;

    @FXML
    private DatePicker dateFrom;

    @FXML
    private DatePicker dateTo;

    @FXML
    private RadioButton radio1;

    @FXML
    private RadioButton radio2;

    @FXML
    private Button search;

    @FXML
    private Button friends;

    @FXML
    private Button export;

    private UserService service;
    private List<String> listToBeExported;
    private PopOver friendsPopOver;
    private ObservableList<HBox> listPop = FXCollections.observableArrayList();
    private Long idFriend;

    @FXML
    public void initialize(){
        radio1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                radio2.setSelected(false);
            }
        });
        listToBeExported = new ArrayList<>();

        radio2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                radio1.setSelected(false);
            }
        });

        search.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listToBeExported.clear();
                listRapoarte.getItems().clear();
                if(radio1.isSelected())
                listToBeExported.addAll(service.raportActivitati(dateFrom.getValue(),
                        dateTo.getValue()));
                else if(radio2.isSelected())
                    listToBeExported.addAll(service.rapoarteMesaje(dateFrom.getValue(),
                            dateTo.getValue(),idFriend));
                listRapoarte.getItems().setAll(listToBeExported);
            }
        });
        export.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream("D:/rapoarte.pdf"));
                    document.open();
                    Anchor anchor = new Anchor("Rapoarte");
                    Chapter chapter = new Chapter(new Paragraph(anchor),1);
                    Section section = chapter.addSection("");
                    for(String s:listToBeExported)
                        section.add(new Paragraph(s));
                    document.add(chapter);
                    document.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        friends.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                listPop.clear();
                for (User u : service.getFriends()) {
                    javafx.scene.image.Image image = u.getPicture();
                    if (image == null)
                        image = new Image("/images/default.jpg");
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageView.setClip(createClip());
                    Button profile = new Button(u.getFirstName() + " " + u.getLastName(), imageView);
                    profile.setGraphicTextGap(15);
                    profile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            idFriend = u.getId();
                        }
                    });
                    HBox hbox = new HBox(profile);
                    listPop.add(hbox);
                }
                ListView<HBox> listView = new ListView(listPop);
                friendsPopOver = new PopOver(listView);
                friendsPopOver.show(friends);
            }
        });
    }

    public void setService(UserService service){
        this.service = service;
    }

        private javafx.scene.shape.Rectangle createClip(){
            javafx.scene.shape.Rectangle clip = new Rectangle();
            clip.setWidth(50);
            clip.setHeight(50);
            clip.setArcHeight(50);
            clip.setArcWidth(50);
            return clip;
        }
}
