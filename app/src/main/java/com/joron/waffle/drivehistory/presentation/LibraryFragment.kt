package com.joron.waffle.drivehistory.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.joron.waffle.drivehistory.databinding.LibraryFragmentBinding
import com.joron.waffle.drivehistory.domain.viewmodel.LibraryViewModel
import com.joron.waffle.drivehistory.presentation.dialog.EditTrackDialog

class LibraryFragment : Fragment() {

    private lateinit var binding: LibraryFragmentBinding
    private var libraryViewModel = LibraryViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        val activity = requireActivity()
        binding = LibraryFragmentBinding.inflate(layoutInflater)
            .apply {
                this.libraryViewModel = this@LibraryFragment.libraryViewModel
                lifecycleOwner = this@LibraryFragment
            }
        lifecycle.addObserver(libraryViewModel)

        val libraryListAdapter = LibraryListAdapter(
            activity,
            emptyList(),
        ) {
            findNavController().navigate(
                LibraryFragmentDirections.libraryToMapNav(it.trackUuid),
            )
//            val intent = Intent(
//                activity,
//                MapActivity::class.java,
//            ).apply {
//                putExtra(KEY_TRACK_UUID, it.trackUuid)
//            }
//            startActivity(intent)
        }
        val listView = binding.libraryList
        listView.adapter = libraryListAdapter
        libraryViewModel.load(activity)
        libraryViewModel.trackList.observe(activity, Observer { item ->
            libraryListAdapter.updateList(item)
        })

        binding.buttonAddTrack.setOnClickListener {
            val editTrackDialog = EditTrackDialog()
            editTrackDialog.setOkListener { title ->
                libraryViewModel.createTrack(activity, title) {
                    findNavController().navigate(
                        LibraryFragmentDirections.libraryToMapNav(it)
                    )
//                    val intent = Intent(
//                        this@LibraryFragment,
//                        MapActivity::class.java,
//                    ).apply {
//                        putExtra(KEY_TRACK_UUID, it)
//                    }
//                    startActivity(intent)
                }
            }
            editTrackDialog.show(
                requireActivity().supportFragmentManager,
                TAG
            )
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onCreateView")
    }

    companion object {
        const val TAG = "LibraryFragment"
    }
}