package com.example.travelmatentt.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelmatentt.R
import com.example.travelmatentt.data.retrofit.ApiService
import com.example.travelmatentt.databinding.FragmentHomeBinding
import com.example.travelmatentt.view.setting.SettingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null
    private lateinit var recommendationsRecyclerView: RecyclerView
    private lateinit var adapter: RecommendationAdapter
    private var accessToken: String? = null
    private var username: String? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        accessToken = sharedPreferences.getString("access_token", null)
        username = sharedPreferences.getString("username", "Guest")

        val greeting = getString(R.string.hi_rizki, username)
        binding?.greetingTextView?.text = greeting

        recommendationsRecyclerView = binding?.recyclerViewRecommendations ?: return
        adapter = RecommendationAdapter { recommendation ->
            val intent = Intent(requireContext(), DestinationDetailActivity::class.java).apply {
                putExtra("nama_objek", recommendation.nama_objek)
                putExtra("deskripsi", recommendation.deskripsi)
                putExtra("picture_url", recommendation.picture_url)
                putExtra("alamat", recommendation.alamat)
                putExtra("rating", recommendation.rating.toString())
                putExtra("estimasi_harga_tiket", recommendation.estimasi_harga_tiket)
            }
            startActivity(intent)
        }

        recommendationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recommendationsRecyclerView.adapter = adapter

        accessToken?.let {
            fetchDestinationRecommendations(it)
        }

        binding?.profileImageView?.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }

        setupSearchBar()

        displayProfileImage()

        binding?.apply {
            btnAirTerjun.setOnClickListener {
                Toast.makeText(requireContext(), "Air Terjun ", Toast.LENGTH_SHORT).show()
            }
            btnBatuKarang.setOnClickListener {
                Toast.makeText(requireContext(), "Batu Karang ", Toast.LENGTH_SHORT).show()
            }
            btnBukit.setOnClickListener {
                Toast.makeText(requireContext(), "Bukit ", Toast.LENGTH_SHORT).show()
            }
            btnDanau.setOnClickListener {
                Toast.makeText(requireContext(), "Danau ", Toast.LENGTH_SHORT).show()
            }
            btnDesaWisata.setOnClickListener {
                Toast.makeText(requireContext(), "Desa Wisata ", Toast.LENGTH_SHORT).show()
            }
            btnGoa.setOnClickListener {
                Toast.makeText(requireContext(), "Goa ", Toast.LENGTH_SHORT).show()
            }
            btnGunung.setOnClickListener {
                Toast.makeText(requireContext(), "Gunung ", Toast.LENGTH_SHORT).show()
            }
            btnPantai.setOnClickListener {
                Toast.makeText(requireContext(), "Pantai ", Toast.LENGTH_SHORT).show()
            }
            btnPulau.setOnClickListener {
                Toast.makeText(requireContext(), "Pulau ", Toast.LENGTH_SHORT).show()
            }
            btnSungai.setOnClickListener {
                Toast.makeText(requireContext(), "Sungai ", Toast.LENGTH_SHORT).show()
            }
            btnTaman.setOnClickListener {
                Toast.makeText(requireContext(), "Taman ", Toast.LENGTH_SHORT).show()
            }
            btnTamanNasional.setOnClickListener {
                Toast.makeText(requireContext(), "Taman Nasional ", Toast.LENGTH_SHORT).show()
            }
            btnTugu.setOnClickListener {
                Toast.makeText(requireContext(), "Tugu ", Toast.LENGTH_SHORT).show()
            }
            btnWisataAlam.setOnClickListener {
                Toast.makeText(requireContext(), "Wisata Alam", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSearchBar() {
        binding?.searchView?.setupWithSearchBar(binding!!.searchBar)

    }


    private fun fetchDestinationRecommendations(token: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://travelmate-ntt-1096623490059.asia-southeast2.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getDestinationRecommendations("Bearer $token")

        call.enqueue(object : Callback<RecommendationResponse> {
            override fun onResponse(
                call: Call<RecommendationResponse>,
                response: Response<RecommendationResponse>
            ) {
                if (response.isSuccessful) {
                    val recommendations = response.body()?.recommendations ?: emptyList()
                    adapter.submitList(recommendations)
                } else {

                    Log.e("HomeFragment", "Failed to fetch recommendations: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RecommendationResponse>, t: Throwable) {
                Log.e("HomeFragment", "Error: ${t.message}")
            }
        })
    }

    private fun displayProfileImage() {
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val imageUriString = sharedPreferences.getString("profile_image_uri", null)

        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding?.profileImageView?.setImageURI(imageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}