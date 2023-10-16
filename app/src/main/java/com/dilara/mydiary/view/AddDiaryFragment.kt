package com.dilara.mydiary.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
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
import com.dilara.mydiary.EMOJI
import com.dilara.mydiary.MONTH
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.EmojiRecyclerViewAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentAddDiaryBinding
import com.dilara.mydiary.viewmodel.AddDiaryViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class AddDiaryFragment : BaseFragment(), EmojiRecyclerViewAdapter.Listener {
    private var binding: FragmentAddDiaryBinding? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var diarySelectedPicture: Uri? = null
    private val viewModel: AddDiaryViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    private lateinit var menuAdapter: EmojiRecyclerViewAdapter
    private val menuList = ArrayList<Int>()
    private var diaryMood: Int? = null
    private var lastClickedItem: View? = null
    private lateinit var today: Calendar
    private var selectedPhoto: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        menuAdapter = EmojiRecyclerViewAdapter(requireContext(), menuList, this@AddDiaryFragment)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            intentFromResult.data
                        )
                        selectedPhoto = makeSmallerBitmap(bitmap, 1000)
                        diarySelectedPicture = Uri.parse(
                            MediaStore.Images.Media.insertImage(
                                requireActivity().contentResolver,
                                selectedPhoto,
                                "a",
                                null
                            )
                        )
                        binding?.addPhoto?.setImageBitmap(bitmap)
                    }
                }
            }

        viewModel.popUpLiveData.observe(requireActivity()) {
            (activity as HomeActivity).showPopUp(
                getString(R.string.warning),
                getString(R.string.try_again),
                getString(R.string.ok)
            )
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

        today = Calendar.getInstance()
        val day: Int = today.get(Calendar.DAY_OF_MONTH)
        val month = MONTH.values().firstOrNull { it.value == today.get(Calendar.MONTH) }
        val year: Int = today.get(Calendar.YEAR)
        val diaryDate = "$day $month $year"
        binding?.selectedDateText?.setText(diaryDate)

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
            val diaryEmoji: Int
            if (diaryMood == null) {
                (activity as HomeActivity).showPopUp(
                    getString(R.string.warning),
                    getString(R.string.select_emoji),
                    getString(R.string.ok)
                )
            } else {
                diaryEmoji = when (diaryMood) {
                    R.drawable.emoji_veryhappy -> EMOJI.VERY_HAPPY.ordinal
                    R.drawable.emoji_happy -> EMOJI.HAPPY.ordinal
                    R.drawable.emoji_expressionless -> EMOJI.EXPRESSIONLESS.ordinal
                    R.drawable.emoji_sad -> EMOJI.SAD.ordinal
                    R.drawable.emoji_cry -> EMOJI.CRY.ordinal
                    R.drawable.emoji_angry -> EMOJI.ANGRY.ordinal
                    else -> {
                        EMOJI.COOL.ordinal
                    }
                }

                val diaryTitle = binding?.diaryTitle?.text.toString()
                val diaryEditText = binding?.writtenDiaryText?.text.toString()

                if (diaryTitle.isNotEmpty()) {
                    if (diaryEditText.isNotEmpty()) {
                        viewModel.upload(
                            diaryDate,
                            diaryTitle,
                            diaryEditText,
                            diaryEmoji,
                            diarySelectedPicture,
                            onSuccess = {
                                (activity as HomeActivity).showPopUp(
                                    getString(R.string.app_name),
                                    getString(R.string.diary_successful),
                                    getString(R.string.ok),
                                    positiveButtonCallBack = {
                                        requireActivity().supportFragmentManager.popBackStack()
                                    }
                                )
                            },
                            onFailure = {
                                (activity as HomeActivity).showPopUp(
                                    getString(R.string.app_name),
                                    getString(R.string.try_again),
                                    getString(R.string.ok)
                                )
                            })
                    } else {
                        (activity as HomeActivity).showPopUp(
                            getString(R.string.warning),
                            getString(R.string.write_diary),
                            getString(R.string.ok)
                        )

                    }
                } else {
                    (activity as HomeActivity).showPopUp(
                        getString(R.string.warning),
                        getString(R.string.write_title),
                        getString(R.string.ok)
                    )
                }
            }
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
        diaryMood = resourceId
        lastClickedItem = itemView
    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var heigth = image.height

        val bitmapRatio: Double = width.toDouble() / heigth.toDouble()

        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeigth = width / bitmapRatio
            heigth = scaledHeigth.toInt()

        } else {
            heigth = maximumSize
            val scaledHeigth = heigth * bitmapRatio
            width = scaledHeigth.toInt()

        }
        return Bitmap.createScaledBitmap(image, width, heigth, true)

    }

}