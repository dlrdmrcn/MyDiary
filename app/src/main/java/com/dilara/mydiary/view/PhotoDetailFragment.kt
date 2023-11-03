package com.dilara.mydiary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.dilara.mydiary.R
import com.dilara.mydiary.base.BaseFragment
import com.dilara.mydiary.databinding.FragmentPhotoDetailBinding
import com.dilara.mydiary.viewmodel.PhotoDetailViewModel
import com.squareup.picasso.Picasso

class PhotoDetailFragment : BaseFragment() {
    private var binding: FragmentPhotoDetailBinding? = null
    private val viewModel: PhotoDetailViewModel by viewModels {
        SavedStateViewModelFactory(this.activity?.application, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: DiaryDetailsFragmentArgs by navArgs()
        val title = safeArgs.title
        val date = safeArgs.date
        val content = safeArgs.content
        val mood = safeArgs.mood
        val downloadUrl = safeArgs.downloadUrl
        val id = safeArgs.id

        Picasso
            .get()
            .load(downloadUrl)
            .into(binding?.selectedPhoto)

        binding?.goToDiary?.setOnClickListener {
            val action =
                PhotoDetailFragmentDirections.actionPhotoDetailFragmentToDiaryDetailsFragment(
                    date,
                    title,
                    content,
                    downloadUrl,
                    mood,
                    id
                )
            Navigation.findNavController(it).navigate(action)
        }

        binding?.deletePhoto?.setOnClickListener {
            (activity as? HomeActivity)?.showPopUp(
                getString(R.string.app_name),
                getString(R.string.delete_photo_message),
                getString(R.string.cancel),
                null,
                getString(R.string.delete_photo),
                negativeButtonCallBack = {
                    viewModel.deletePhoto(downloadUrl!!,
                        onSuccess = {
                            (activity as? HomeActivity)?.showPopUp(
                                getString(R.string.app_name),
                                getString(R.string.delete_photo_success),
                                getString(R.string.ok), positiveButtonCallBack = {
                                    requireActivity().supportFragmentManager.popBackStack()
                                })
                        },
                        onFailure = {
                            (activity as? HomeActivity)?.showPopUp(
                                getString(R.string.app_name),
                                getString(R.string.try_again),
                                getString(R.string.ok)
                            )
                        })
                },
                getString(R.string.delete_button_contentDescription),
                neutralButtonCallBack = {
                    viewModel.deleteDiary(id,
                        onSuccess = {
                            (activity as? HomeActivity)?.showPopUp(
                                getString(R.string.app_name),
                                getString(R.string.delete_diary_success),
                                getString(R.string.ok), positiveButtonCallBack = {
                                    requireActivity().supportFragmentManager.popBackStack()
                                })
                        },
                        onFailure = {
                            (activity as? HomeActivity)?.showPopUp(
                                getString(R.string.app_name),
                                getString(R.string.try_again),
                                getString(R.string.ok)
                            )
                        })
                }

            )
        }


    }

}