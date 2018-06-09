/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author austinweir
 * @see https://www.primefaces.org/showcase/ui/data/datatable/lazy.xhtml
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

public class LazyItemDataModel extends LazyDataModel<Item> {
    private List<Item> datasource;
    
    public LazyItemDataModel(List<Item> datasource) {
        this.datasource = datasource;
    }
    
    public void addItem(Item item) {
        datasource.add(item);
    }
    
    @Override
    public Item getRowData(String rowKey) {
        for(Item item : datasource) {
            System.out.println("Item Row: " + rowKey);
            System.out.println("Item Id: " + String.valueOf(item.getId()));
            if(String.valueOf(item.getId()).equals(rowKey)) {
                System.out.println("Returning Item!!");
                return item;
            }
        }
        
        return null;
    }
    
    @Override
    public Object getRowKey(Item item) {
        return String.valueOf(item.getId());
    }
    
    @Override
    public List<Item> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {
        List<Item> data = new ArrayList<>();
        
        for (Item item : datasource) {
            boolean match = true;
            
            if (filters != null) {
                for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                    try {
                        String filterProperty = it.next();
                        Object filterValue = filters.get(filterProperty);
                        String fieldValue = String.valueOf(item.getClass().getField(filterProperty).get(item));
                        
                        if ((filterValue == null) || (fieldValue.startsWith(filterValue.toString()))) {
                            match = true;
                        }
                        else {
                            match = false;
                            break;
                        }
                    } catch(Exception e) {
                        match = false;
                    }
                }
            }
            if (match) {
                data.add(item);
            }
        }
        
        if (sortField != null) {
            Collections.sort(data, new LazySorter(sortField, sortOrder));
        }
        
        int dataSize = data.size();
        this.setRowCount(dataSize);
        
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        }
        else {
            return data;
        }
    }
}
