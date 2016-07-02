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

class DetailViewController: UIViewController, GMSMapViewDelegate, CLLocationManagerDelegate {
    
    var camera : GMSCameraPosition!
    var marker: GMSMarker!
    var chooseMarker: GMSMarker!
    var place: GMSPlace!
    var markerView:UIImageView!
    var placesClient: GMSPlacesClient!
    
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
        
        // http connection
        httpJson()
        
        // GMSPlace example
        let placeID = "AIzaSyBE45rf_8Sbg5sqeCTeD6lvhxlwZuTX2eU"
        
    }
    func mapView(mapView: GMSMapView, didTapAtCoordinate coordinate: CLLocationCoordinate2D) {
        print("You tapped at \(coordinate.latitude), \(coordinate.longitude)")
        let tapPosition = CLLocationCoordinate2D(latitude: coordinate.latitude, longitude: coordinate.longitude)
        marker.position = tapPosition
        marker.title = "Try to get the name from google api"
        marker.map = mapView
    }
    
    func httpJson() {
        print ("in httpJason()")
        let url: NSURL = NSURL(string: "http://47.90.38.75/test.html")!
        //let url: NSURL = NSURL(string: "http://10.201.120.159/test.html")!
        let request: NSURLRequest = NSURLRequest(URL: url)
        let response: AutoreleasingUnsafeMutablePointer<NSURLResponse?>=nil
        do{
            
            let dataVal = try NSURLConnection.sendSynchronousRequest(request, returningResponse: response)
            
            print(response)
            
            do {
                if let jsonResult = try NSJSONSerialization.JSONObjectWithData(dataVal, options: []) as? NSDictionary {
                    print("Synchronous\(jsonResult)")
                }
            } catch let error as NSError {
                print(error.localizedDescription)
            }
            
        }catch let error as NSError
        {
            print(error.localizedDescription)
        }
    }
}


// Handle the user's selection.
extension DetailViewController: GMSAutocompleteResultsViewControllerDelegate {
    
    func resultsController(resultsController: GMSAutocompleteResultsViewController!,
                           didAutocompleteWithPlace place: GMSPlace!) {
        searchController?.active = false
        // Do something with the selected place.
        print("Place name: ", place.name)
        print("Place address: ", place.formattedAddress)
        print("Place attributions: ", place.attributions)
        
        
        
        chooseMarker.position.latitude = place.coordinate.latitude
        chooseMarker.position.longitude = place.coordinate.longitude
        chooseMarker.title = place.name
        //chooseMarker.map = mapView
        mapView.animateToLocation(chooseMarker.position)
        
        
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
