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
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dilara.mydiary.enums.EMOJI
import com.dilara.mydiary.enums.MONTH
import com.dilara.mydiary.R
import com.dilara.mydiary.adapter.EmojiRecyclerViewAdapter
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentAddDiaryBinding
import com.dilara.mydiary.model.Diary
import com.dilara.mydiary.viewmodel.AddDiaryViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.io.File
import java.util.Calendar
import java.util.UUID

class AddDiaryFragment : BaseFragment(), EmojiRecyclerViewAdapter.Listener {
    private var binding: FragmentAddDiaryBinding? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPictureUri: Uri? = null
    private val viewModel: AddDiaryViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }
    private lateinit var emojiAdapter: EmojiRecyclerViewAdapter
    private val menuList = ArrayList<Int>()
    private var diaryMood: Int? = null
    private var lastClickedItem: View? = null
    private lateinit var today: Calendar
    private var selectedBitmap: Bitmap? = null
    private var fromEdit = false
    private var diaryArgs: Diary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val safeArgs: AddDiaryFragmentArgs by navArgs()
            fromEdit = safeArgs.fromEdit
            if (fromEdit) {
                diaryArgs = Diary(
                    safeArgs.date ?: "",
                    safeArgs.content ?: "",
                    safeArgs.title ?: "",
                    safeArgs.mood,
                    safeArgs.downloadUrl ?: "",
                    safeArgs.id ?: ""
                )
            }
        }

        emojiAdapter = EmojiRecyclerViewAdapter(
            requireContext(),
            menuList,
            this@AddDiaryFragment,
            getSelectedMoodDrawable(diaryArgs?.mood ?: -1)
        )

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            intentFromResult.data
                        )
                        selectedBitmap = makeSmallerBitmap(bitmap, 1000)
                        selectedPictureUri = Uri.parse(
                            MediaStore.Images.Media.insertImage(
                                requireActivity().contentResolver,
                                selectedBitmap,
                                "a",
                                null
                            )
                        )
                        binding?.addPhoto?.setImageBitmap(bitmap)
                    }
                }
            }

        viewModel.popUpLiveData.observe(requireActivity()) {
            (activity as? HomeActivity)?.showPopUp(
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
        val date = "$day $month $year"

        if (fromEdit) {
            binding?.diaryTitle?.setText(diaryArgs?.title)
            binding?.selectedDateText?.setText(diaryArgs?.date)
            binding?.writtenDiaryText?.setText(diaryArgs?.content)

            if (!diaryArgs?.downloadUrl.isNullOrEmpty()) {
                if (viewModel.auth.uid != null) {
                    Picasso
                        .get()
                        .load(diaryArgs?.downloadUrl)
                        .into(binding?.addPhoto)
                } else {
                    Picasso
                        .get()
                        .load(File(diaryArgs?.downloadUrl ?: ""))
                        .into(binding?.addPhoto)
                }
            } else {
                //No operation
            }
        } else {
            binding?.selectedDateText?.setText(date)
        }

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding?.recyclerViewEmoji?.layoutManager = layoutManager
        binding?.recyclerViewEmoji?.adapter = emojiAdapter

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
            val mood: Int
            if (diaryMood == null) {
                (activity as? HomeActivity)?.showPopUp(
                    getString(R.string.warning),
                    getString(R.string.select_emoji),
                    getString(R.string.ok)
                )
            } else {
                mood = getMood()

                val title = binding?.diaryTitle?.text.toString()
                val content = binding?.writtenDiaryText?.text.toString()
                val lastDate = binding?.selectedDateText?.text.toString()

                if (title.isNotEmpty()) {
                    if (content.isNotEmpty()) {
                        val safeArgs: AddDiaryFragmentArgs by navArgs()
                        fromEdit = safeArgs.fromEdit
                        if (fromEdit) {
                            if (safeArgs.id.isNullOrEmpty()) {
                                val uuid = UUID.randomUUID().toString()
                                val diaryList = Diary(
                                    lastDate,
                                    content,
                                    title,
                                    mood.toLong(),
                                    selectedPictureUri?.path ?: "",
                                    uuid
                                )
                                viewModel.upload(
                                    lastDate,
                                    title,
                                    content,
                                    mood,
                                    selectedPictureUri,
                                    onSuccess = {
                                        (activity as? HomeActivity)?.showPopUp(
                                            getString(R.string.app_name),
                                            getString(R.string.diary_successful),
                                            getString(R.string.ok),
                                            positiveButtonCallBack = {
                                                requireActivity().supportFragmentManager.popBackStack()
                                            }
                                        )
                                    },
                                    onFailure = {
                                        (activity as? HomeActivity)?.showPopUp(
                                            getString(R.string.app_name),
                                            getString(R.string.try_again),
                                            getString(R.string.ok)
                                        )
                                    }, requireActivity().applicationContext,
                                    diaryList, selectedBitmap
                                )
                            } else {
                                safeArgs.id?.let { id ->
                                    val diary = Diary(
                                        lastDate,
                                        content,
                                        title,
                                        mood.toLong(),
                                        selectedPictureUri?.path ?: "",
                                        id
                                    )
                                    diary.roomId = safeArgs.roomId
                                    viewModel.update(
                                        requireContext(),
                                        id,
                                        lastDate,
                                        title,
                                        content,
                                        mood,
                                        selectedPictureUri,
                                        onSuccess = {
                                            (activity as? HomeActivity)?.showPopUp(
                                                getString(R.string.app_name),
                                                getString(R.string.diary_successful),
                                                getString(R.string.ok),
                                                positiveButtonCallBack = {
                                                    val action =
                                                        AddDiaryFragmentDirections.actionAddDiaryFragmentToHomeFragment()
                                                    Navigation.findNavController(it)
                                                        .navigate(action)
                                                }
                                            )
                                        },
                                        onFailure = {
                                            (activity as? HomeActivity)?.showPopUp(
                                                getString(R.string.app_name),
                                                getString(R.string.try_again),
                                                getString(R.string.ok)
                                            )
                                        },
                                        diary,
                                        selectedBitmap
                                    )
                                }
                            }

                        } else {
                            val uuid = UUID.randomUUID().toString()
                            val diaryList = Diary(
                                lastDate,
                                content,
                                title,
                                mood.toLong(),
                                selectedPictureUri?.path ?: "",
                                uuid
                            )
                            viewModel.upload(
                                lastDate,
                                title,
                                content,
                                mood,
                                selectedPictureUri,
                                onSuccess = {
                                    (activity as? HomeActivity)?.showPopUp(
                                        getString(R.string.app_name),
                                        getString(R.string.diary_successful),
                                        getString(R.string.ok),
                                        positiveButtonCallBack = {
                                            requireActivity().supportFragmentManager.popBackStack()
                                        }
                                    )
                                },
                                onFailure = {
                                    (activity as? HomeActivity)?.showPopUp(
                                        getString(R.string.app_name),
                                        getString(R.string.try_again),
                                        getString(R.string.ok)
                                    )
                                }, requireActivity().applicationContext,
                                diaryList, selectedBitmap
                            )
                        }

                    } else {
                        (activity as? HomeActivity)?.showPopUp(
                            getString(R.string.warning),
                            getString(R.string.write_diary),
                            getString(R.string.ok)
                        )

                    }
                } else {
                    (activity as? HomeActivity)?.showPopUp(
                        getString(R.string.warning),
                        getString(R.string.write_title),
                        getString(R.string.ok)
                    )
                }
            }
        }
    }

    private fun getMood(): Int {
        return when (diaryMood) {
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
    }

    private fun getSelectedMoodDrawable(mood: Long): Int {
        return when (mood.toInt()) {
            EMOJI.VERY_HAPPY.ordinal -> R.drawable.emoji_veryhappy
            EMOJI.HAPPY.ordinal -> R.drawable.emoji_happy
            EMOJI.EXPRESSIONLESS.ordinal -> R.drawable.emoji_expressionless
            EMOJI.SAD.ordinal -> R.drawable.emoji_sad
            EMOJI.CRY.ordinal -> R.drawable.emoji_cry
            EMOJI.ANGRY.ordinal -> R.drawable.emoji_angry
            EMOJI.COOL.ordinal -> R.drawable.emoji_cool
            else -> -1
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
        intentToGallery.setType("image/jpeg")
        activityResultLauncher.launch(intentToGallery)
    }

    override fun onItemClick(resourceId: Int, itemView: View?) {

        itemView?.let {
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