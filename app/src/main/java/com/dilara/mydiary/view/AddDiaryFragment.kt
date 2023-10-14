package com.dilara.mydiary.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.EmojiRecyclerViewAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentAddDiaryBinding
import com.dilara.mydiary.viewmodel.AddDiaryViewModel
import com.google.android.material.snackbar.Snackbar

class AddDiaryFragment : BaseFragment(), EmojiRecyclerViewAdapter.Listener {
    private var binding: FragmentAddDiaryBinding? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private val viewModel: AddDiaryViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    lateinit var menuAdapter: EmojiRecyclerViewAdapter
    val menuList = ArrayList<Int>()
    private var mood: Int? = null
    var lastClickedItem: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuAdapter = EmojiRecyclerViewAdapter(requireContext(), menuList, this@AddDiaryFragment)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding?.addPhoto?.setImageURI(it)
                        }
                    }
                }
            }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddDiaryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.recyclerViewEmoji?.layoutManager = layoutManager
        binding?.recyclerViewEmoji?.adapter = menuAdapter

        menuList.add(R.drawable.emoji_veryhappy)
        menuList.add(R.drawable.emoji_happy)
        menuList.add(R.drawable.emoji_expressionless)
        menuList.add(R.drawable.emoji_sad)
        menuList.add(R.drawable.emoji_cry)
        menuList.add(R.drawable.emoji_angry)
        menuList.add(R.drawable.emoji_cool)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    navigateToGallery()
                } else {
                    Snackbar.make(
                        view,
                        getString(R.string.permission_needed_for_gallery),
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(getString(R.string.give_permission)) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", requireContext().packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }.show()

                }
            }

        binding?.addPhoto?.setOnClickListener {
            onAddPhotoClicked(view)
        }

        binding?.save?.setOnClickListener {
            val title = binding?.diaryTitle?.text.toString()
            val editText = binding?.writtenDiaryText?.text.toString()
            viewModel.upload(selectedPicture, title, editText, onFailure = {}, onSuccess = {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
            })
        }
    }

    private fun onAddPhotoClicked(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PERMISSION_GRANTED
        ) {
            navigateToGallery()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
        ) {
            navigateToGallery()
        } else if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PERMISSION_GRANTED
        ) {
            navigateToGallery()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                showSnackBarForPermission(view)
            } else {
                requestPermission()
            }
        }
    }

    private fun showSnackBarForPermission(view: View) {
        Snackbar.make(view, R.string.permission_needed_for_gallery, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.give_permission) {
                requestPermission()
            }.show()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun navigateToGallery() {
        val intentToGallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityResultLauncher.launch(intentToGallery)
    }

    override fun onItemClick(resourceId: Int, itemView: View) {

        if (lastClickedItem != null && lastClickedItem != itemView) {
            lastClickedItem?.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent
                )
            )
        }
        itemView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.emoji_background
            )
        )
        mood = resourceId
        lastClickedItem = itemView
    }

}