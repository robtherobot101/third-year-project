using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Text;

namespace mobileAppClient.Models.CustomObjects
{
    /*
     * An extension of an Observable collection that will only fire an onchanged event once
     * for a batch insert, will utilise for infinite scroll on User Search list
     */
    public class CustomObservableCollection<T> : ObservableCollection<T>
    {
        public CustomObservableCollection()
            : base()
        {
        }

        public CustomObservableCollection(IEnumerable<T> collection)
            : base(collection)
        {
        }

        public CustomObservableCollection(List<T> list)
            : base(list)
        {
        }

        /*
         * Adds an IEnumerable of data to the collection
         */
        public void AddRange(IEnumerable<T> range)
        {
            foreach (var item in range)
            {
                Items.Add(item);
            }

            this.OnPropertyChanged(new PropertyChangedEventArgs("Count"));
            this.OnPropertyChanged(new PropertyChangedEventArgs("Item[]"));
            this.OnCollectionChanged(new NotifyCollectionChangedEventArgs(NotifyCollectionChangedAction.Reset));
        }

        /*
         * Replaces the data with the given IEnumerable
         */
        public void Reset(IEnumerable<T> range)
        {
            this.Items.Clear();

            AddRange(range);
        }
    }
}
