package gov.nist.spectrumbrowser.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Button;

import gov.nist.spectrumbrowser.common.AbstractSpectrumBrowserWidget;
import gov.nist.spectrumbrowser.common.Defines;
import gov.nist.spectrumbrowser.common.SpectrumBrowserCallback;
import gov.nist.spectrumbrowser.common.SpectrumBrowserScreen;


public class ServiceControl extends AbstractSpectrumBrowserWidget implements SpectrumBrowserScreen, SpectrumBrowserCallback<String> {

	private HorizontalPanel titlePanel;
	private Grid grid;
	HTML html;
	
	private Map<String, TextBox> statusBoxMap;
	private Map<String, Button> stopButtonMap;
	private Map<String, Button> restartButtonMap;
	private TextBox AdminBox;
	private TextBox SpecBrowBox;
	private TextBox StreamBox;
	private TextBox OccuBox;
	private TextBox SysMonBox;
	private Boolean AdminBool;
	private Boolean SpecBrowBool;
	private Boolean StreamBool;
	private Boolean OccuBool;
	private Boolean SysMonBool;
	private Button AdminStopButton;
	private Button SpecBrowStopButton;
	private Button StreamStopButton;
	private Button OccuStopButton;
	private Button SysMonStopButton;
	private Button AdminRestartButton;
	private Button SpecBrowRestartButton;
	private Button StreamRestartButton;
	private Button OccuRestartButton;
	private Button SysMonRestartButton;
	private static String[] SERVICE_NAMES = Defines.SERVICE_NAMES;
	private static int NUM_SERVICES = Defines.SERVICE_NAMES.length;
	private static int STATUS_CHECK_TIME_SEC = 5;

	private Admin admin;
	private static Logger logger = Logger.getLogger("SpectrumBrowser");

	private static final String END_LABEL = "Service Control";

	public ServiceControl(Admin admin) {
		super();
		try {
			this.admin = admin;	
			titlePanel = new HorizontalPanel();
			statusBoxMap = new HashMap<String, TextBox>();
			stopButtonMap = new HashMap<String, Button>();
			restartButtonMap = new HashMap<String, Button>();
			AdminBox = new TextBox();
			SpecBrowBox = new TextBox();
			StreamBox = new TextBox();
			OccuBox = new TextBox();
			SysMonBox = new TextBox();
			AdminStopButton = new Button();
			SpecBrowStopButton = new Button();
			StreamStopButton = new Button();
			OccuStopButton = new Button();
			SysMonStopButton = new Button();
			AdminRestartButton = new Button();
			SpecBrowRestartButton = new Button();
			StreamRestartButton = new Button();
			OccuRestartButton = new Button();
			SysMonRestartButton = new Button();
			statusBoxMap.put(SERVICE_NAMES[0], AdminBox);
			statusBoxMap.put(SERVICE_NAMES[1], SpecBrowBox);
			statusBoxMap.put(SERVICE_NAMES[2], StreamBox);
			statusBoxMap.put(SERVICE_NAMES[3], OccuBox);
			statusBoxMap.put(SERVICE_NAMES[4], SysMonBox);
			stopButtonMap.put(SERVICE_NAMES[0], AdminStopButton);
			stopButtonMap.put(SERVICE_NAMES[1], SpecBrowStopButton);
			stopButtonMap.put(SERVICE_NAMES[2], StreamStopButton);
			stopButtonMap.put(SERVICE_NAMES[3], OccuStopButton);
			stopButtonMap.put(SERVICE_NAMES[4], SysMonStopButton);
			restartButtonMap.put(SERVICE_NAMES[0], AdminRestartButton);
			restartButtonMap.put(SERVICE_NAMES[1], SpecBrowRestartButton);
			restartButtonMap.put(SERVICE_NAMES[2], StreamRestartButton);
			restartButtonMap.put(SERVICE_NAMES[3], OccuRestartButton);
			restartButtonMap.put(SERVICE_NAMES[4], SysMonRestartButton);

			setStatusBox();
			
		} catch (Throwable th) {
			logger.log(Level.SEVERE, "Problem contacting server", th);
			Window.alert("Problem contacting server");
			admin.logoff();
		}
	}
	
	private void drawMenuItems() {
		HTML title;
		title = new HTML("<h3> The statuses of the project services are shown below </h3>");
		titlePanel.add(title);
		verticalPanel.add(titlePanel);		
	}
	
	@Override
	public void draw() {
		try {
			verticalPanel.clear();
			titlePanel.clear();			
			drawMenuItems();
			
			grid = new Grid(NUM_SERVICES + 1, 4);
			grid.setCellSpacing(4);
			grid.setBorderWidth(2);
			verticalPanel.add(grid);
			
			grid.setText(0, 0, "Service");
			grid.setText(0, 1, "Status");
			grid.setText(0, 2, "Stop");
			grid.setText(0, 3, "Restart");
			
			for (int i = 0; i < NUM_SERVICES; i++) {
				grid.setText(i + 1, 0, SERVICE_NAMES[i]);
				grid.setWidget(i + 1, 1, statusBoxMap.get(SERVICE_NAMES[i]));

				if (i!=0){
					createStopButton(i);
					createRestartButton(i);
					grid.setWidget(i + 1, 2, stopButtonMap.get(SERVICE_NAMES[i]));
					grid.setWidget(i + 1, 3, restartButtonMap.get(SERVICE_NAMES[i]));
				}
				else{
					grid.setText(i + 1, 2, "N/A");
					grid.setText(i + 1, 3, "N/A");
				}
				
			}
			
		} catch (Throwable th) {
			logger.log(Level.SEVERE, "ERROR drawing service control screen", th);
		}
	}
	
	private void setStatusBox() {

		Timer timer = new Timer() {
			String status = "";
			@Override
			public void run() {
				for (int i = 0; i < NUM_SERVICES; i++) {
					final String serviceName = SERVICE_NAMES[i];

					Admin.getAdminService().getServiceStatus(serviceName, new SpectrumBrowserCallback<String>() {
						@Override
						public void onSuccess(String result) {
							JSONObject jsonObj = JSONParser.parseLenient(result).isObject();
							if (jsonObj.get("status").isString().stringValue().equals("OK")) {
								status = jsonObj.get("serviceStatus").isString().stringValue();
								statusBoxMap.get(serviceName).setText(status);
								stopButtonMap.get(serviceName).setEnabled(true);
								restartButtonMap.get(serviceName).setEnabled(true);
							} else {
								String errorMessage = jsonObj.get("ErrorMessage").isString().stringValue();
								//Window.alert("Error getting service status. Please refresh. Error Message : "+errorMessage);
								status = "ERROR";
							}
						}

						@Override
						public void onFailure(Throwable throwable) {
							Window.alert("Error communicating with server");
							admin.logoff();
						}
					});
				}
			}
		};
	    timer.scheduleRepeating(STATUS_CHECK_TIME_SEC*1000);
	}
	
	public void createStopButton(final int i){
		final String serviceName = SERVICE_NAMES[i];

		stopButtonMap.get(serviceName).removeStyleName("gwt-Button");
		stopButtonMap.get(serviceName).getElement().getStyle().setBackgroundColor("red");
		stopButtonMap.get(serviceName).setHeight("50px");
		stopButtonMap.get(serviceName).setWidth("50px");
		stopButtonMap.get(serviceName).addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Admin.getAdminService().stopService(SERVICE_NAMES[i],
						new SpectrumBrowserCallback<String>() {

					@Override
					public void onSuccess(String result) {
						JSONObject jsonObj = JSONParser.parseLenient(result).isObject();
						if (!(jsonObj.get("status").isString().stringValue().equals("OK"))) {
							String errorMessage = jsonObj.get("ErrorMessage").isString().stringValue();
							Window.alert("Error stopping service. Please refresh. Error Message : "+errorMessage);
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						Window.alert("Error communicating with server");
						admin.logoff();
					}
				});
				stopButtonMap.get(serviceName).setEnabled(false);
			}
		});
	}
	
	public void createRestartButton(final int i){
		final String serviceName = SERVICE_NAMES[i];
		
		restartButtonMap.get(serviceName).removeStyleName("gwt-Button");
		restartButtonMap.get(serviceName).getElement().getStyle().setBackgroundColor("green");
		restartButtonMap.get(serviceName).setHeight("50px");
		restartButtonMap.get(serviceName).setWidth("50px");
		restartButtonMap.get(serviceName).addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Admin.getAdminService().restartService(SERVICE_NAMES[i],
						new SpectrumBrowserCallback<String>() {

					@Override
					public void onSuccess(String result) {
						JSONObject jsonObj = JSONParser.parseLenient(result).isObject();
						if (!(jsonObj.get("status").isString().stringValue().equals("OK"))) {
							String errorMessage = jsonObj.get("ErrorMessage").isString().stringValue();
							Window.alert("Error restarting service. Please refresh. Error Message : "+errorMessage);
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						Window.alert("Error communicating with server");
						admin.logoff();
					}
				});
				restartButtonMap.get(serviceName).setEnabled(false);
			}
		});
	}

	@Override
	public String getLabel() {
		return END_LABEL + " >>";
	}

	@Override
	public String getEndLabel() {
		return END_LABEL;
	}

	@Override
	public void onSuccess(String jsonString) {}

	@Override
	public void onFailure(Throwable throwable) {
		logger.log(Level.SEVERE, "Error Communicating with server",
				throwable);
		admin.logoff();
	}
	
	@Override
	public String toString() {
		return null;
	}

}
