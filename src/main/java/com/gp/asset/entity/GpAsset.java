package com.gp.asset.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gp_assets")
public class GpAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String assetTypeName;

    private String amcStatus;
    private String block;
    private String lgdCode;

    @Column(unique = true)
    private Long assetId;

    private String availability;

    private String district;

    public GpAsset() {}

    public GpAsset(String name, String assetTypeName, String amcStatus,
                   Long assetId, String availability, String district,String block,String lgdCode) {
        this.name = name;
        this.assetTypeName = assetTypeName;
        this.amcStatus = amcStatus;
        this.assetId = assetId;
        this.availability = availability;
        this.district = district;
        this.block = block;
        this.lgdCode = lgdCode;
    }

	public String getLgdCode() {
		return lgdCode;
	}

	public void setLgdCode(String lgdCode) {
		this.lgdCode = lgdCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAssetTypeName() {
		return assetTypeName;
	}

	public void setAssetTypeName(String assetTypeName) {
		this.assetTypeName = assetTypeName;
	}

	public String getAmcStatus() {
		return amcStatus;
	}

	public void setAmcStatus(String amcStatus) {
		this.amcStatus = amcStatus;
	}

	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}
	public String getBlock() {
	    return block;
	}

	public void setBlock(String block) {
	    this.block = block;
	}

   
}