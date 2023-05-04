package com.example.projecttwo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.util.*

private const val TAG = "GeocacheListFragment"
class GeocacheListFragment: Fragment() {
    interface Callbacks {
        fun onGeocacheSelected(geocacheID: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var geocacheRecycler: RecyclerView
    private var adapter: GeocacheAdapter? = GeocacheAdapter(emptyList())

    private val geocacheListViewModel: GeocacheListViewModel by lazy {
        ViewModelProviders.of(this).get(GeocacheListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_geocache_list, container, false)
        geocacheRecycler = view.findViewById(R.id.geocache_recycler_view) as RecyclerView
        geocacheRecycler.layoutManager = LinearLayoutManager(context)
        geocacheRecycler.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocacheListViewModel.geocachesLiveData.observe(
            viewLifecycleOwner,
            Observer { geocaches ->
                geocaches?.let {
                    Log.i(TAG, "Got geocaches ${geocaches.size}")
                    updateUI(geocaches)
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_geocache_list, menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_geocache -> {
                val geocache = Geocache()
                geocacheListViewModel.addGeocache(geocache)
                callbacks?.onGeocacheSelected(geocache.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(geocaches: List<Geocache>) {
        adapter = GeocacheAdapter(geocaches)
        geocacheRecycler.adapter = adapter
    }

    companion object {
        fun newInstance(): GeocacheListFragment {
            return GeocacheListFragment()
        }
    }

    private inner class GeocacheHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var geocache: Geocache
        private val cacheName: TextView = itemView.findViewById(R.id.geocache_name)
        private val rating: ImageView = itemView.findViewById(R.id.rating)
        private val cacheCity: TextView = itemView.findViewById(R.id.geocache_city)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(geocache: Geocache) {
            this.geocache = geocache
            cacheName.text = this.geocache.name
            cacheCity.text = this.geocache.city

            when (geocache.rating) {
                0 -> rating.setImageResource(R.drawable.rating0)
                1 -> rating.setImageResource(R.drawable.rating1)
                2 -> rating.setImageResource(R.drawable.rating2)
                3 -> rating.setImageResource(R.drawable.rating3)
                4 -> rating.setImageResource(R.drawable.rating4)
                5 -> rating.setImageResource(R.drawable.rating5)
                else -> {
                    Log.e(TAG, "${geocache.rating} is not a valid rating")
                }
            }
        }

        override fun onClick(p0: View?) {
            callbacks?.onGeocacheSelected(geocache.id)
        }
    }

    // List controller
    private inner class GeocacheAdapter(var caches: List<Geocache>): RecyclerView.Adapter<GeocacheHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeocacheHolder {
            val view = layoutInflater.inflate(R.layout.list_item_geocache, parent, false)
            return GeocacheHolder(view)
        }

        override fun getItemCount(): Int {
            return caches.size
        }

        override fun onBindViewHolder(holder: GeocacheHolder, position: Int) {
            val cache = caches[position]
            holder.bind(cache)
        }
    }
}