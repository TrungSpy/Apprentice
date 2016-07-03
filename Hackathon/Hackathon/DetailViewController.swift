//
//  DetailViewController.swift
//  Hackathon
//
//  Created by legend507 on 7/2/16.
//  Copyright © 2016 legend507. All rights reserved.
//

import UIKit
import MapKit
import GoogleMaps

struct Spots {
    var id: Int
    var name: String
    var latitude: Double
    var longitude: Double
    
    init(inputId: Int, inputName: String, inputLatitude: Double, inputLongitude: Double) {
        id = inputId
        name = inputName
        latitude = inputLatitude
        longitude = inputLongitude
    }
}


class DetailViewController: UIViewController, GMSMapViewDelegate {
    var camera : GMSCameraPosition!
    var marker: GMSMarker!
    var place: GMSPlace!
    var markerView:UIImageView!
    var placesClient: GMSPlacesClient!
    var downloadedList: [Spots] = []
    var downloadedMarker: [GMSMarker] = []
    var hasUpdated = 0
    
    var resultsViewController: GMSAutocompleteResultsViewController?
    var searchController: UISearchController?
    var resultView: UITextView?
    
    let locationManager = CLLocationManager()

    @IBOutlet weak var detailDescriptionLabel : UILabel!
    @IBOutlet var mapView : GMSMapView!
    @IBOutlet weak var myPlaceOut: UILabel!
    
    var detailItem: AnyObject? {
        didSet {
            // Update the view.
            self.configureView()
        }
    }

    func configureView() {
        // Update the user interface for the detail item.
        if let detail = self.detailItem {
            if let label = self.detailDescriptionLabel {
                label.text = detail.description
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        resultsViewController = GMSAutocompleteResultsViewController()
        resultsViewController?.delegate = self
        
        searchController = UISearchController(searchResultsController: resultsViewController)
        searchController?.searchResultsUpdater = resultsViewController
        
        // Put the search bar in the navigation bar.
        searchController?.searchBar.sizeToFit()
        self.navigationItem.titleView = searchController?.searchBar
        
        // When UISearchController presents the results view, present it in
        // this view controller, not one further up the chain.
        self.definesPresentationContext = true
        
        // Prevent the navigation bar from being hidden when searching.
        searchController?.hidesNavigationBarDuringPresentation = false
        
        
        
    }

    
    override func loadView() {
        print("in loadView()")
        placesClient = GMSPlacesClient()
        camera = GMSCameraPosition.cameraWithLatitude(35.0, longitude: 139.0, zoom: 6)
        mapView = GMSMapView.mapWithFrame(CGRectZero, camera: camera)
        mapView.myLocationEnabled = true
        mapView.mapType = kGMSTypeNormal
        mapView.delegate = self
        self.view = mapView
        let position = CLLocationCoordinate2D(latitude: 34.60, longitude: 139.50)
        marker = GMSMarker(position: position)
        marker.title = "Tokyo"
        marker.map = mapView
        
        // GMSPlace example
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        
    }
    func mapView(mapView: GMSMapView, didTapAtCoordinate coordinate: CLLocationCoordinate2D) {
        print("You tapped at \(coordinate.latitude), \(coordinate.longitude)")
        let tapPosition = CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        
        marker.position = tapPosition
        marker.title = "User's Interest"
        marker.map = mapView
    }
    
    func httpJson() {
        print ("in httpJason()")
        downloadedList.removeAll()
        downloadedMarker.removeAll()
        let url: NSURL = NSURL(string: "http://10.201.120.198/data?sid=spot-data")!
        //let url: NSURL = NSURL(string: "http://10.201.120.159/test.html")!
        let request: NSURLRequest = NSURLRequest(URL: url)
        let response: AutoreleasingUnsafeMutablePointer<NSURLResponse?>=nil
        do{
            
            let dataVal = try NSURLConnection.sendSynchronousRequest(request, returningResponse: response)
            
            print(response)
            
            do {
                if let jsonResult = try NSJSONSerialization.JSONObjectWithData(dataVal, options: []) as? NSDictionary {
                    print("Synchronous\(jsonResult)")
                    
                    if let dataList = jsonResult["dataList"] as? [[String: AnyObject]] {
                        for data in dataList {
                            var oneSpot = Spots(inputId: 0, inputName: "", inputLatitude: 0.0, inputLongitude: 0.0)
                            var oneMarker: GMSMarker
                            if let id = data["m_spot_id"] as? Int {
                                oneSpot.id = id
                            }
                            if let name = data["name"] as? String {
                                oneSpot.name = name
                            }
                            if let latitude = data["latitude"] as? Double {
                                oneSpot.latitude = latitude
                            }
                            if let longitude = data["longitude"] as? Double {
                                oneSpot.longitude = longitude
                            }
                            let position = CLLocationCoordinate2D(latitude: oneSpot.latitude, longitude: oneSpot.longitude)
                            oneMarker = GMSMarker(position: position)
                            oneMarker.title = oneSpot.name
                            downloadedMarker.append(oneMarker)
                            print(oneSpot.name)
                            downloadedList.append(oneSpot)
                        }
                    }
                    
                }
            } catch let error as NSError {
                print(error.localizedDescription)
            }
            
        }catch let error as NSError
        {
            print(error.localizedDescription)
        }
    }
    
    
    @IBAction func downloadButton(sender: UIButton) {
        print("download button pressed!")
        // http connection
        httpJson()
        hasUpdated = 1
        for var oneMarker in downloadedMarker {
            if oneMarker.title != "test" {
                oneMarker.map = mapView
            }
        }
    }
    
}


// Handle the user's selection - search function
extension DetailViewController: GMSAutocompleteResultsViewControllerDelegate {
    
    func resultsController(resultsController: GMSAutocompleteResultsViewController!,
                           didAutocompleteWithPlace place: GMSPlace!) {
        searchController?.active = false
        // Do something with the selected place.
        print("Place name: ", place.name)
        print("Place address: ", place.formattedAddress)
        print("Place attributions: ", place.attributions)
        marker.position.latitude = place.coordinate.latitude
        marker.position.longitude = place.coordinate.longitude
        marker.title = place.name
        marker.map = mapView
        mapView.animateToLocation(marker.position)
    }
    
    
    func resultsController(resultsController: GMSAutocompleteResultsViewController!,
                           didFailAutocompleteWithError error: NSError!){
        // TODO: handle the error.
        print("Error: ", error.description)
        
        
    }
    
    
    // Turn the network activity indicator on and off again.
    func didRequestAutocompletePredictionsForResultsController(resultsController: GMSAutocompleteResultsViewController!) {
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
    }
    
    func didUpdateAutocompletePredictionsForResultsController(resultsController: GMSAutocompleteResultsViewController!) {
        UIApplication.sharedApplication().networkActivityIndicatorVisible = false
    }
}


// MARK: - CLLocationManagerDelegate - User location
//1
extension DetailViewController: CLLocationManagerDelegate {
    // 2
    func locationManager(manager: CLLocationManager, didChangeAuthorizationStatus status: CLAuthorizationStatus) {
        // 3
        if status == .AuthorizedWhenInUse {
            
            // 4
            locationManager.startUpdatingLocation()
            
            //5
            mapView.myLocationEnabled = true
            mapView.settings.myLocationButton = true
        }
    }
    
    // 6
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.first {
            
            // 7
            mapView.camera = GMSCameraPosition(target: location.coordinate, zoom: 15, bearing: 0, viewingAngle: 0)
            
            // 8
            locationManager.stopUpdatingLocation()
        }
        
    }
}
