package net.cyclestreets.api;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import android.os.AsyncTask;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;

public class POICategories implements Iterable<POICategory>
{
  private final List<POICategory> cats_;
  
  private POICategories()
  {
    cats_ = new ArrayList<POICategory>();
  } // POICategories
  
  private void add(final POICategory cat) { cats_.add(cat); }
  
  public int count() { return cats_.size(); }
  public POICategory get(int index) { return cats_.get(index); }
  public Iterator<POICategory> iterator() { return cats_.iterator(); }
  
  static public Factory<POICategories> factory() { 
    return new POICategoriesFactory();
  } // factory
  
  static private class POICategoriesFactory extends Factory<POICategories>
  {    
    private POICategories cats_;
    private String key_;
    private String name_;
    private String shortName_;
    
    @Override
    protected ContentHandler contentHandler()
    {
      cats_ = new POICategories();
      
      final RootElement root = new RootElement("poitypes");
      final Element item = root.getChild("poitypes").getChild("poitype");
      item.setStartElementListener(new StartElementListener() {
        @Override
        public void start(Attributes attributes)
        {
          key_ = null;
          name_ = null;
          shortName_ = null;
        }
      });
      item.setEndElementListener(new EndElementListener(){
          public void end() {
            cats_.add(new POICategory(key_, shortName_, name_));
          }
      });
      item.getChild("key").setEndTextElementListener(new EndTextElementListener(){
          public void end(String body) {
            key_ = body;
          }
      });
      item.getChild("name").setEndTextElementListener(new EndTextElementListener(){
          public void end(String body) {
            name_ = body;
          }
      });
      item.getChild("shortname").setEndTextElementListener(new EndTextElementListener(){
          public void end(String body) {
            shortName_ = body;
          }
      });

      return root.getContentHandler();
    } // contentHandler

    @Override
    protected POICategories get()
    {
      return cats_;
    } // get
  } // POICategories

  //////////////////////////////////////////////
  static private POICategories loaded_;
  
  static public POICategories get() 
  {
    if(loaded_ == null)
      load();
    return loaded_;
  } // get
  
  static public void load()
  {
    try {
      loaded_ = ApiClient.getPOICategories();
    }
    catch(Exception e) {
      // ah
    }
  } // load
  
  static public void backgroundLoad()
  {
    new GetPOICategoriesTask().execute();
  } // backgroundLoad
  
  static private class GetPOICategoriesTask extends AsyncTask<Void,Void,POICategories>
  {
    protected POICategories doInBackground(Void... params) 
    {
      try {
        return ApiClient.getPOICategories();
      }
      catch (final Exception ex) {
        // never mind, eh?
      }
      return null;
    } // doInBackground
    
    @Override
    protected void onPostExecute(final POICategories cats) 
    {
      POICategories.loaded_ = cats;
    } // onPostExecute
  } // GetPOICategoriesTask
} // class POICategories